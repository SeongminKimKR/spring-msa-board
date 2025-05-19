package my.board.articleread.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LikeClient(
    @Value("\${endpoints.my-board-like-service.url}")
    private val likeServiceUrl: String,
) {
    private val logger = LoggerFactory.getLogger(LikeClient::class.java)

    private val restClient: RestClient by lazy {
        RestClient.create(likeServiceUrl)
    }

    fun count(articleId: Long): Long {
        return try {
            restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java) ?: 0L
        } catch (e: Exception) {
            logger.error("[LikeClient.count] articleId={}", articleId, e)
            return 0L
        }
    }
}
