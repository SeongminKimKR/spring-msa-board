package my.board.articleread.client

import my.board.articleread.cache.OptimizedCacheable
import my.board.articleread.repository.ArticleQueryModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
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

    // 레디스에서 데이터를 조회
    // 레디스에 데이터가 없었다면, count 메소드 내부 로직히 호출되면서, viewService로 원본 데이터를 요청한다. 그리고, 레디스에 데이터를 넣고 응답한다.
    // 레디스에 데이터가 있었다면, 그 데이터를 그대로 바로 반환한다.
//    @Cacheable(key = "#articleId", value = ["articleViewCount"])
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1L)
    fun count(articleId: Long): Long {
        logger.info("[ViewClient.count] articleId={}", articleId)
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
