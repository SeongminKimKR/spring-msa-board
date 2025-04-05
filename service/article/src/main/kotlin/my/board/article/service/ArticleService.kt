package my.board.article.service

import my.board.common.snowflake.Snowflake
import my.board.article.entity.Article
import my.board.article.repository.ArticleRepository
import my.board.article.service.request.ArticleCreateRequest
import my.board.article.service.request.ArticleUpdateRequest
import my.board.article.service.response.ArticleResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article(snowflake.nextId(), request.boardId, request.writerId, request.title, request.content)
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
        return ArticleResponse.from(article)
    }

    fun read(articleId: Long) :ArticleResponse{
        val article = articleRepository.findByIdOrNull(articleId) ?: throw IllegalArgumentException()
        return ArticleResponse.from(article)
    }

    @Transactional
    fun delete(articleId: Long) = articleRepository.deleteById(articleId)
}
