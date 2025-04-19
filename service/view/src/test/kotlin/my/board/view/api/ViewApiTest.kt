package my.board.view.api

import io.kotest.core.spec.style.FunSpec
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class ViewApiTest : FunSpec() {
    val restClient = RestClient.create("http://localhost:9003")

    init {
        test("viewTest") {

            val executorService = Executors.newFixedThreadPool(100)
            val latch = CountDownLatch(10000)

            for(i in 0 until  10000) {
                executorService.submit{
                    restClient.post()
                        .uri("/v1/article-views/articles/{articleId}/users/{userId}", 4L, 1L)
                        .retrieve()
                        .body(Long::class.java)
                    latch.countDown()
                }
            }

            latch.await()

            val count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", 4L)
                .retrieve()
                .body(Long::class.java)

            println("count = $count")
        }
    }
}
