package my.board.hotarticle.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@SpringBootTest
class HotArticleListRepositoryTest(
    private val hotArticleListRepository: HotArticleListRepository,
) : FunSpec({
    test("addTest") {
        val time = LocalDateTime.of(2025, 5, 11, 0 ,0)
        val limit = 3L

        hotArticleListRepository.add(1L, time, 2L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(2L, time, 3L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(3L, time, 1L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(4L, time, 5L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(5L, time, 4L, limit, Duration.ofSeconds(3))

        val articleIds = hotArticleListRepository.readAll("20250511")

        articleIds.size shouldBe limit
        articleIds[0] shouldBe 4L
        articleIds[1] shouldBe 5L
        articleIds[2] shouldBe 2L

        TimeUnit.SECONDS.sleep(5)

        hotArticleListRepository.readAll("20250511").isEmpty() shouldBe true
    }
})
