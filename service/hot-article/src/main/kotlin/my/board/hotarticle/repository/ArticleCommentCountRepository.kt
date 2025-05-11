package my.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleCommentCountRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun createOrUpdate(
        articleId: Long,
        commentCount: Long,
        ttl: Duration
    ) {
        redisTemplate.opsForValue().set(generateKey(articleId), commentCount.toString(), ttl)
    }

    fun read(articleId: Long) = redisTemplate.opsForValue()
        .get(generateKey(articleId))
        ?.toLong() ?: 0L

    private fun generateKey(
        articleId: Long
    ) = KEY_FORMAT.format(articleId)

    companion object {
        // hot-article::article::{articleId}::comment-count
        private const val KEY_FORMAT = "hot-article::article::%s::comment-count"
    }
}
