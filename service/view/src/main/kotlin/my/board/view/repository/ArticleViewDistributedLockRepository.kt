package my.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewDistributedLockRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun lock(
        articleId: Long,
        userId: Long,
        ttl: Duration,
    ): Boolean {
        val key = generateKey(articleId, userId)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl) ?: false
    }

    private fun generateKey(
        articleId: Long,
        userId: Long,
    ): String = KEY_FORMAT.format(articleId, userId)

    companion object {
        //view::article::{article_id}::user::{user_id}::lock
        private const val KEY_FORMAT = "/view::article::%s::user::%s::lock"
    }
}
