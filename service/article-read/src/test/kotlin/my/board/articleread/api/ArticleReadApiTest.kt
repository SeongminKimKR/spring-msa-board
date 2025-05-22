package my.board.articleread.api

import my.board.articleread.service.response.ArticleReadPageResponse
import my.board.articleread.service.response.ArticleReadResponse
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import kotlin.test.Test


class ArticleReadApiTest {
    val articleReadRestClient = RestClient.create("http://localhost:9005")
    val articleRestClient = RestClient.create("http://localhost:9000")

    @Test
    fun readTest() {
        val response = articleReadRestClient.get()
            .uri("/v1/articles/{articleId}", 166764931713650708)
            .retrieve()
            .body(ArticleReadResponse::class.java)

        println("response = $response")
    }

    @Test
    fun readAllTest() {
        val response1 = articleReadRestClient.get()
            .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(1L, 3000L, 5))
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        println("response1.articleCount = ${response1!!.articleCount}")

        for(article in response1.articles) {
            println("article.articleId = ${article.articleId}")
        }

        val response2 = articleRestClient.get()
            .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(1L, 3000L, 5))
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        println("response1.articleCount = ${response2!!.articleCount}")

        for(article in response2.articles) {
            println("article.articleId = ${article.articleId}")
        }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val responses1 = articleReadRestClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".format(1L, 5L, 183930060876582912))
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleReadResponse>>() {})!!

        for(response in responses1) {
            println("response = ${response.articleId}")
        }

        val responses2 = articleRestClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".format(1L, 5L, 183930060876582912))
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleReadResponse>>() {})!!

        for(response in responses2) {
            println("response = ${response.articleId}")
        }

        // 183930060876582912

    }
}
