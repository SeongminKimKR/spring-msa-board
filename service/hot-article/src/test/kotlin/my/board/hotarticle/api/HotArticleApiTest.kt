package my.board.hotarticle.api

import my.board.hotarticle.service.response.HotArticleResponse
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import kotlin.test.Test

class HotArticleApiTest {
    private val restClient = RestClient.create("http://localhost:9004")

    @Test
    fun readAllTest() {
        val responses = restClient.get()
            .uri("/v1/hot-articles/articles/date/{dateStr}", "20250516")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<HotArticleResponse>>() {})

        for(response in responses ?: emptyList()) {
            println("response = $response")
        }
    }
}
