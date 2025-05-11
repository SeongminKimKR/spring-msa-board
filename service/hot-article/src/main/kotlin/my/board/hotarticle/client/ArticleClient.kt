package my.board.hotarticle.client

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.my-board-article-service.url}")
    private val articleServerUrl: String
) {
    private val logger = LoggerFactory.getLogger(ArticleClient::class.java)

    private val restClient: RestClient by lazy {
        RestClient.create(articleServerUrl)
    }

    fun read(articleId: Long): ArticleResponse? {
        return try {
            restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (e: Exception) {
            logger.error("[ArticleClient.read] articleId={}", articleId, e)
            null
        }
    }

    companion object {
        data class ArticleResponse (
            val articleId: Long,
            val title: String,
            val createdAt: LocalDateTime
        )
    }
}
