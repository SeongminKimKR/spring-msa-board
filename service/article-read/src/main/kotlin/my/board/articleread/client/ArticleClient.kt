package my.board.articleread.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.util.Optional
import kotlin.math.log

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

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticlePageResponse {
        return try {
            restClient.get()
                .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(boardId, page, pageSize))
                .retrieve()
                .body(ArticlePageResponse::class.java)
                ?: ArticlePageResponse.EMPTY
        } catch (e: Exception) {
            logger.error("[ArticleClient.readAll] boardId={}, page={}, pageSize={}", boardId, page, pageSize, e)
            ArticlePageResponse.EMPTY
        }
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        lastArticleId: Long?,
        pageSize: Long,
    ): List<ArticleResponse> {
        return try {
            restClient.get()
                .uri(
                    lastArticleId?.let {
                        "/v1/articles/infinite-scroll?boardId=%s&lastArticleId=%s&pageSize=%s"
                            .format(boardId, lastArticleId, pageSize)
                    }
                        ?: "/v1/articles/infinite-scroll?boardId=%s&pageSize=%s"
                            .format(boardId, pageSize)
                )
                .retrieve()
                .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})
                ?: emptyList()
        } catch (e: Exception) {
            logger.error(
                "[ArticleClient.readAllInfiniteScroll] boardId={}, lastArticleId={}, pageSize={}",
                boardId, lastArticleId, pageSize, e
            )
            emptyList()
        }
    }

    fun count(boardId: Long): Long {
        return try {
            restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", boardId)
                .retrieve()
                .body(Long::class.java)
                ?: 0L
        } catch (e: Exception) {
            logger.error("[ArticleClient.count] boardId={}", boardId, e)
            0L
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

        data class ArticlePageResponse(
            val articles: List<ArticleResponse>,
            val articleCount: Long,
        ) {
            companion object {
                val EMPTY = ArticlePageResponse(emptyList(), 0L)
            }
        }
    }
}
