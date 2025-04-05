package my.board.article.controller

import io.kotest.core.spec.style.FunSpec
import my.board.article.service.request.ArticleCreateRequest
import my.board.article.service.request.ArticleUpdateRequest
import my.board.article.service.response.ArticleResponse
import org.springframework.web.client.RestClient

class ArticleControllerTest : FunSpec({
    val restClient = RestClient.create("http://localhost:9000")

    val create: (ArticleCreateRequest) -> ArticleResponse? = { request ->
        restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    val read : (Long) -> ArticleResponse? = { articleId ->
        restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    val update : (Long) -> ArticleResponse? = { articleId ->
        restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(ArticleUpdateRequest("hi 2", "my content22"))
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    val delete : (Long) -> Unit = { articleId ->
        restClient.delete()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .toBodilessEntity()
    }

    test("create") {
        val request = ArticleCreateRequest("hi", "my content", 1L, 1L)
        val response = create(request)
        println(response)
    }

    test("read") {
        val response = read(166761214677094400L)
        println(response)
    }

    test("update") {
        val response = update(166761214677094400L)
        println(response)
    }

    test("delete") {
        val response = delete(166761214677094400L)
        println(response)
    }
})

