package my.board.articleread.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.util.Optional

@Component
class ArticleClient(
    @Value("\${endpoints.my-board-article-service.url}")
    private val articleServiceUrl: String,
) {
    private val logger = LoggerFactory.getLogger(ArticleClient::class.java)

    private val restClient: RestClient by lazy {
        RestClient.create(articleServiceUrl)
    }

    fun read(articleId: Long): ArticleResponse? {
        return try {
            restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (e: Exception) {
            logger.error("[ArticleClient.read] articleId={}", articleId, e)
            return null
        }
    }
    companion object {
        data class ArticleResponse(
            val articleId: Long,
            val title: String,
            val content: String,
            val boardId: Long,
            val writerId: Long,
            val createdAt: LocalDateTime,
            val modifiedAt: LocalDateTime,
        )
    }
}
