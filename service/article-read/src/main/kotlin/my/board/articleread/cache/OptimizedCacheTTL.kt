package my.board.articleread.cache

import java.time.Duration

data class OptimizedCacheTTL(
    val logicalTTL: Duration,
    val physicalTTL: Duration,
) {
    companion object {
        private const val PHYSICAL_TTL_DELAY_SECONDS = 5L

        fun of(ttlSeconds: Long) = OptimizedCacheTTL(
            logicalTTL = Duration.ofSeconds(ttlSeconds),
            physicalTTL = Duration.ofSeconds(ttlSeconds).plusSeconds(PHYSICAL_TTL_DELAY_SECONDS)
        )
    }
}
