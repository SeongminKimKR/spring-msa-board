package my.board.articleread.service

import my.board.articleread.client.ArticleClient
import my.board.articleread.client.CommentClient
import my.board.articleread.client.LikeClient
import my.board.articleread.client.ViewClient
import my.board.articleread.repository.ArticleIdListRepository
import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.articleread.repository.BoardArticleCountRepository
import my.board.articleread.service.eventhandler.EventHandler
import my.board.articleread.service.response.ArticleReadPageResponse
import my.board.articleread.service.response.ArticleReadResponse
import my.board.common.event.Event
import my.board.common.event.EventPayload
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.math.log

@Service
class ArticleReadService(
    private val articleClient: ArticleClient,
    private val commentClient: CommentClient,
    private val likeClient: LikeClient,
    private val viewClient: ViewClient,
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
    private val eventHandlers: List<EventHandler<*>>,
) {
    private val logger = LoggerFactory.getLogger(ArticleReadService::class.java)

    fun handleEvent(event: Event<EventPayload>) {
        val supportedEventHandlers = findEventHandler(event)

        supportedEventHandlers.forEach { it.handle(event) }
    }

    fun read(articleId: Long): ArticleReadResponse {
        val articleQueryModel = articleQueryModelRepository.read(articleId)
            ?: fetch(articleId)
            ?: throw IllegalArgumentException("Invalid articleId=$articleId")

        return ArticleReadResponse.from(
            articleQueryModel,
            viewClient.count(articleId)
        )
    }

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticleReadPageResponse {
        return ArticleReadPageResponse.of(
            readAll(readAllArticleIds(boardId, page, pageSize)),
            count(boardId)
        )
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        lastArticleId: Long?,
        pageSize: Long,
    ): List<ArticleReadResponse> {
        return readAll(
            readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize)
        )
    }

    private fun findEventHandler(event: Event<EventPayload>): List<EventHandler<EventPayload>> =
        eventHandlers.filterIsInstance<EventHandler<EventPayload>>()
            .filter { it.supports(event) }// 안전하게 캐스팅

    private fun fetch(articleId: Long): ArticleQueryModel? {
        val articleQueryModel = articleClient.read(articleId)?.let { article ->
            ArticleQueryModel.create(
                article,
                commentClient.count(articleId),
                likeClient.count(articleId),
            )
        }

        articleQueryModel?.let { articleQueryModelRepository.create(it, Duration.ofDays(1)) }

        logger.info(
            "[ArticleReadService.fetch] fetch data. articleId={}, isPresent={}", articleId, articleQueryModel != null
        )

        return articleQueryModel
    }

    private fun readAll(articleIds: List<Long>): List<ArticleReadResponse> {
        val articleQueryModelMap: Map<Long, ArticleQueryModel> = articleQueryModelRepository.readAll(articleIds)
        return articleIds.mapNotNull { articleId -> articleQueryModelMap[articleId] ?: fetch(articleId) }
            .map { articleQueryModel ->
                ArticleReadResponse.from(
                    articleQueryModel,
                    viewClient.count(articleQueryModel.articleId)
                )
            }
    }

    private fun readAllArticleIds(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): List<Long> {
        val articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize)
        return if (pageSize == articleIds.size.toLong()) {
            logger.info("[ArticleReadService.readAllArticleIds] return redis data.")
            articleIds
        } else {
            logger.info("[ArticleReadService.readAllArticleIds] return origin data.")
            articleClient.readAll(boardId, page, pageSize)
                .articles.map(ArticleClient.Companion.ArticleResponse::articleId)
        }
    }

    private fun count(boardId: Long): Long {
        return boardArticleCountRepository.read(boardId)
    }

    private fun readAllInfiniteScrollArticleIds(
        boardId: Long,
        lastArticleId: Long?,
        pageSize: Long,
    ): List<Long> {
        val articleIds = articleIdListRepository.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
        return if(pageSize == articleIds.size.toLong()) {
            logger.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data.")
            articleIds
        } else {
            logger.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data.")
            articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
                .map(ArticleClient.Companion.ArticleResponse::articleId)
        }
    }
}
