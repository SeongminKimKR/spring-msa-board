package my.board.articleread.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class ViewClientTest(
    private val viewClient: ViewClient,
) : FunSpec({

    test("readCacheableTest") {
        viewClient.count(1L) // 로그 출력
        viewClient.count(1L) // 로그 미출력
        viewClient.count(1L) // 로그 미출력

        TimeUnit.SECONDS.sleep(3)

        viewClient.count(1L) // 로그 출력
    }

    test("readCacheableMultiThreadTest") {
        val executorService = Executors.newFixedThreadPool(5)

        viewClient.count(1L)

        for(i in 0 until 5) {
            val latch = CountDownLatch(5)
            for(j in 0 until 5) {
                executorService.submit(
                    {
                        viewClient.count(1L)
                        latch.countDown()
                    }
                )
            }

            latch.await()
            TimeUnit.SECONDS.sleep(2)
            println("=== cache expired ===")
        }
    }
})
