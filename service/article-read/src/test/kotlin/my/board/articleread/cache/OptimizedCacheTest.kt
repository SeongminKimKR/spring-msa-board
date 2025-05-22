package my.board.articleread.cache

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class OptimizedCacheTest : FunSpec() {


    init {
        test("parseData") {
            parseDataTest("data", 10)
            parseDataTest(3L, 10)
            parseDataTest(1, 10)
            parseDataTest(TestClass("hihi"), 10)
        }

        test("isExpired") {
            OptimizedCache.of("data", Duration.ofDays(-30)).isExpired() shouldBe true
            OptimizedCache.of("data", Duration.ofDays(30)).isExpired() shouldBe false
        }
    }

    private fun parseDataTest(
        data: Any,
        ttlSeconds: Long,
    ) {
        val optimizedCache = OptimizedCache.of(data, Duration.ofSeconds(ttlSeconds))

        println("optimizedCache = $optimizedCache")

        val resolvedData = optimizedCache.parseData(data.javaClass)

        println("resolvedData = $resolvedData")

        resolvedData shouldBe data
    }

    companion object {
        data class TestClass(
            val testData: String,
        )
    }
}
