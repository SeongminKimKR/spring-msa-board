package my.board.articleread.cache

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class OptimizedCacheTTLTest : FunSpec({

    test("of") {
        val ttlSeconds = 10L

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)

        optimizedCacheTTL.logicalTTL shouldBe Duration.ofSeconds(ttlSeconds)
        optimizedCacheTTL.physicalTTL shouldBe Duration.ofSeconds(ttlSeconds).plusSeconds(5L)
    }
})
