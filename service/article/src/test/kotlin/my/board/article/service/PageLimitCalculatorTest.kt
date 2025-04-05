package my.board.article.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PageLimitCalculatorTest : FunSpec({

    test("calculatePageLimit") {
        val result = PageLimitCalculator.calculatePageLimit(1L, 30L, 10L)
        val result1 = PageLimitCalculator.calculatePageLimit(7L, 30L, 10L)
        val result2 = PageLimitCalculator.calculatePageLimit(10L, 30L, 10L)
        val result3 = PageLimitCalculator.calculatePageLimit(11L, 30L, 10L)

        result shouldBe 301L
        result1 shouldBe 301L
        result2 shouldBe 301L
        result3 shouldBe 601L
    }
})
