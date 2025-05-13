package my.board.article.service

import my.board.article.entity.Article
import my.board.article.entity.BoardArticleCount
import my.board.article.repository.ArticleRepository
import my.board.article.repository.BoardArticleCountRepository
import my.board.article.service.request.ArticleCreateRequest
import my.board.article.service.request.ArticleUpdateRequest
import my.board.article.service.response.ArticlePageResponse
import my.board.article.service.response.ArticleResponse
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.ArticleDeletedEventPayload
import my.board.common.event.payload.ArticleUpdatedEventPayload
import my.board.common.outboxmessagerelay.OutboxEventPublisher
import my.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article(snowflake.nextId(), request.boardId, request.writerId, request.title, request.content)
        )

        val result = boardArticleCountRepository.increase(request.boardId)

        if (result == 0) {
            boardArticleCountRepository.save(BoardArticleCount(request.boardId, 1L))
        }

        outboxEventPublisher.publish(
            EventType.ARTICLE_CREATED,
            ArticleCreatedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                boardArticleCount = count(article.boardId)
            ),
            article.boardId,
        )

        return ArticleResponse.from(article)
    }

    @Transactional
    fun update(
        articleId: Long,
        request: ArticleUpdateRequest,
    ): ArticleResponse {
        val article = articleRepository.findByIdOrNull(articleId) ?: throw IllegalArgumentException()
        article.update(request.title, request.content)

        outboxEventPublisher.publish(
            EventType.ARTICLE_UPDATED,
            ArticleUpdatedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            ),
            article.boardId,
        )

        return ArticleResponse.from(article)
    }

    fun read(articleId: Long): ArticleResponse {
        val article = articleRepository.findByIdOrNull(articleId) ?: throw IllegalArgumentException()
        return ArticleResponse.from(article)
    }

    @Transactional
    fun delete(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow()

        articleRepository.delete(article )
        boardArticleCountRepository.decrease(article .boardId)

        outboxEventPublisher.publish(
            EventType.ARTICLE_DELETED,
            ArticleDeletedEventPayload(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                boardArticleCount = count(article.boardId)
            ),
            article.boardId,
        )
    }

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticlePageResponse {
        val articles =
            articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).map { ArticleResponse.from(it) }
        val count = articleRepository.count(boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))

        return ArticlePageResponse(articles, count)
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?,
    ): List<ArticleResponse> {
        val articles = lastArticleId?.let {
            articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId)
        } ?: articleRepository.findAllInfiniteScroll(boardId, pageSize)

        return articles.map { ArticleResponse.from(it) }
    }

    fun count(boardId: Long): Long = boardArticleCountRepository.findById(boardId)
        .map(BoardArticleCount::articleCount)
        .orElse(0L)
}
