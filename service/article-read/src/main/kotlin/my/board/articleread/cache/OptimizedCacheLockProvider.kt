package my.board.articleread.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider (
    private val redisTemplate: StringRedisTemplate,
){
    fun lock(key: String): Boolean {
        return redisTemplate.opsForValue().setIfPresent(
            generateLockKey(key),
            "",
            LOCK_TTL
        ) ?: false
    }

    fun unlock(key: String) {
        redisTemplate.delete(generateLockKey(key))
    }

    private fun generateLockKey(key: String) = KEY_PREFIX + key

    companion object {
        private const val KEY_PREFIX = "optimized-cache-lok::"
        private val LOCK_TTL = Duration.ofSeconds(3)
    }
}
