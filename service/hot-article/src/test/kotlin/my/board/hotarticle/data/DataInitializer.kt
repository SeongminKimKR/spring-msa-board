package my.board.hotarticle.data

import org.springframework.web.client.RestClient
import java.util.random.RandomGenerator
import kotlin.test.Test

class DataInitializer {
    private val articleServiceClient = RestClient.create("http://localhost:9000")
    private val commentServiceClient = RestClient.create("http://localhost:9001")
    private val likeServiceClient = RestClient.create("http://localhost:9002")
    private val viewServiceClient = RestClient.create("http://localhost:9003")


    @Test
    fun initialize() {
        for (i in 0 until 30) {
            val articleId = createArticle()
            val commentCount = RandomGenerator.getDefault().nextLong(10)
            val likeCount = RandomGenerator.getDefault().nextLong(10)
            val viewCount = RandomGenerator.getDefault().nextLong(200)

            createComment(articleId, commentCount)
            like(articleId, likeCount)
            view(articleId, viewCount)
        }
    }

    private fun createArticle(): Long {
        return articleServiceClient.post()
            .uri("/v1/articles")
            .body(ArticleCreateRequest("title", "content", 1L, 1L))
            .retrieve()
            .body(ArticleResponse::class.java)!!
            .articleId
    }

    private fun createComment(articleId: Long, commentCount: Long) {
        repeat(commentCount.toInt()) {
            commentServiceClient.post()
                .uri("/v2/comments")
                .body(CommentCreateRequest(articleId, "content", 1L))
                .retrieve()
                .toBodilessEntity()

        }
    }

    private fun like(articleId: Long, likeCount: Long) {
        for (i in 1..likeCount) {
            likeServiceClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1", articleId, i)
                .retrieve()
                .toBodilessEntity()

        }
    }

    private fun view(articleId: Long, viewCount: Long) {
        for (i in 1..viewCount) {
            viewServiceClient.post()
                .uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, i)
                .retrieve()
                .toBodilessEntity()

        }
    }

    companion object {
        data class ArticleResponse(val articleId: Long)

        data class ArticleCreateRequest(
            val title: String,
            val content: String,
            val writerId: Long,
            val boardId: Long,
        )

        data class CommentCreateRequest(
            val articleId: Long,
            val content: String,
            val writerId: Long,
        )
    }
}
