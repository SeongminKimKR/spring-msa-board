package my.board.articleread.api

import my.board.articleread.service.response.ArticleReadResponse
import org.springframework.web.client.RestClient
import kotlin.test.Test


class ArticleReadApiTest {
    val restClient = RestClient.create("http://localhost:9005")

    @Test
    fun readTest() {
        val response = restClient.get()
            .uri("/v1/articles/{articleId}", 166764931713650708)
            .retrieve()
            .body(ArticleReadResponse::class.java)

        println("response = $response")
    }
}
