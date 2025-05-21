package my.board.articleread.learning

import io.kotest.core.spec.style.FunSpec
import java.math.BigDecimal

class LongToDoubleTest : FunSpec({

    test("longToDoubleTest") {
        val longValue = 111_111_111_111_111_111L
        println("longValue = $longValue")
        val doubleValue = longValue.toDouble()
        println("doubleValue = ${BigDecimal.valueOf(doubleValue).toString()}")
        val longValue2 = doubleValue.toLong()
        println("longValue2 = $longValue2")
    }
})
