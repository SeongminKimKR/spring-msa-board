package my.board.like.api

import io.kotest.core.spec.style.FunSpec
import my.board.like.service.response.ArticleLikeResponse
import org.springframework.web.client.RestClient

class LikeApiTest : FunSpec() {
    val restClient = RestClient.create("http://localhost:9002")

    val create: (Long, Long) -> Unit = { articleId, userId ->
        restClient.post()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
            .retrieve()
            .toBodilessEntity()
    }

    val delete: (Long, Long) -> Unit = { articleId, userId ->
        restClient.delete()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
            .retrieve()
            .toBodilessEntity()
    }

    val read: (Long, Long) -> ArticleLikeResponse? = { articleId, userId ->
        restClient.get()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
            .retrieve()
            .body(ArticleLikeResponse::class.java)
    }


    init {
        test("like") {
            val articleId = 9999L
            create(articleId, 1L)
            create(articleId, 2L)
            create(articleId, 3L)

            val response1 = read(articleId, 1L)
            val response2 = read(articleId, 2L)
            val response3 = read(articleId, 3L)

            println("response1 = $response1")
            println("response2 = $response2")
            println("response3 = $response3")

            delete(articleId, 1L)
            delete(articleId, 2L)
            delete(articleId, 3L)
        }

    }
}

