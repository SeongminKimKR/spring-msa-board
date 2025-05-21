package my.board.articleread.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
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
})
