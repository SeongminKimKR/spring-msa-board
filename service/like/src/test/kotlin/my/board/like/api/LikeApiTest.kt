package my.board.like.api

import io.kotest.core.spec.style.FunSpec
import my.board.like.service.response.ArticleLikeResponse
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LikeApiTest : FunSpec() {
    val restClient = RestClient.create("http://localhost:9002")

    val create: (Long, Long, String) -> Unit = { articleId, userId, lockType ->
        restClient.post()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}/$lockType", articleId, userId)
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

    private fun likePerformanceTest(
        executorService: ExecutorService,
        articleId: Long,
        lockType: String,
    ) {
        val latch = CountDownLatch(3000)
        println("$lockType start")

        create(articleId, 1L, lockType)

        val start = System.nanoTime()
        for(i in 0 until 3000) {
            val userId = (i + 2).toLong()
            executorService.submit {
                try {
                    create(articleId, userId, lockType)
                } catch (e: Exception) {
                    // 예외가 발생하면 아무것도 하지 않음
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        val end = System.nanoTime()

        println("lockType = $lockType time = ${(end- start) / 1000000}ms")
        println("$lockType end")


        val count = restClient.get()
            .uri("/v1/article-likes/articles/{articleId}/count", articleId)
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }
    init {
        test("like") {
            val articleId = 9999L
            create(articleId, 1L, "pessimistic-lock-1")
            create(articleId, 2L, "pessimistic-lock-1")
            create(articleId, 3L, "pessimistic-lock-1")

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

        test("likePerformanceTest") {
            val executorService = Executors.newFixedThreadPool(100)

            likePerformanceTest(executorService, 1111L, "pessimistic-lock-1")
            likePerformanceTest(executorService, 2222L, "pessimistic-lock-2")
            likePerformanceTest(executorService, 3333L, "optimistic-lock")
        }
    }
}

