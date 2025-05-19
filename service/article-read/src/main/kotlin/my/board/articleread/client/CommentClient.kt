package my.board.articleread.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class CommentClient(
    @Value("\${endpoints.my-board-comment-service.url}")
    private val commentServiceUrl: String,
) {
    private val logger = LoggerFactory.getLogger(CommentClient::class.java)

    private val restClient: RestClient by lazy {
        RestClient.create(commentServiceUrl)
    }

    fun count(articleId: Long): Long {
        return try {
            restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java) ?: 0L
        } catch (e: Exception) {
            logger.error("[CommentClient.count] articleId={}", articleId, e)
            return 0L
        }
    }
}
