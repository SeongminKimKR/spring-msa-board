package my.board.articleread.service

import my.board.articleread.client.ArticleClient
import my.board.articleread.client.CommentClient
import my.board.articleread.client.LikeClient
import my.board.articleread.client.ViewClient
import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.articleread.service.eventhandler.EventHandler
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
}
