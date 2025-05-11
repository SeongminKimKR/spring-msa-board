package my.board.hotarticle.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object TimeCalculatorUtils {
    fun calculateDurationMidnight(): Duration {
        val now = LocalDateTime.now()
        val midnight = now.plusDays(1).with(LocalTime.MIDNIGHT)
        return Duration.between(now, midnight)
    }
}
