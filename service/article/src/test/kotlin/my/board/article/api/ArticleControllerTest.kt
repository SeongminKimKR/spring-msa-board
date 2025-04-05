package my.board.article.api

import io.kotest.core.spec.style.FunSpec
import my.board.article.service.request.ArticleCreateRequest
import my.board.article.service.request.ArticleUpdateRequest
import my.board.article.service.response.ArticlePageResponse
import my.board.article.service.response.ArticleResponse
import org.springframework.core.ParameterizedTypeReference
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
        val response = delete(166800323129114624)
        println(response)
    }

    test("readAll") {
        val response = restClient.get()
            .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
            .retrieve()
            .body(ArticlePageResponse::class.java)!!

        println("response.getArticleCount() = ${response.articleCount}")

        for(article in response.articles) {
            println("articleId = ${article.articleId}")
        }
    }

    test("readAllInfiniteScroll") {
        val response = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})!!


        println("firstPage")
        for(article in response) {
            println("articleId = ${article.articleId}")
        }

        val lastArticleId = response.last().articleId

        val response2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".format(lastArticleId))
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})!!


        println("secondPage")
        for(article in response2) {
            println("articleId = ${article.articleId}")
        }
    }
})

