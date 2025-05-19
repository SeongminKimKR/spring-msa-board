package my.board.articleread.client

import my.board.articleread.repository.ArticleQueryModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ViewClient(
    @Value("\${endpoints.my-board-view-service.url}")
    private val viewServiceUrl: String,
) {
    private val logger = LoggerFactory.getLogger(ViewClient::class.java)

    private val restClient: RestClient by lazy {
        RestClient.create(viewServiceUrl)
    }

    fun count(articleId: Long): Long {
        return try {
            restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java) ?: 0L
        } catch (e: Exception) {
            logger.error("[ViewClient.count] articleId={}", articleId, e)
            return 0L
        }
    }
}
