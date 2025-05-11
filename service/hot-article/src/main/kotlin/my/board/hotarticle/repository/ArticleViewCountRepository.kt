package my.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewCountRepository (
    private val redisTemplate: StringRedisTemplate,
){
    fun createOrUpdate(
        articleId: Long,
        viewCount: Long,
        ttl: Duration
    ) {
        redisTemplate.opsForValue()
            .set(generateKey(articleId), viewCount.toString(), ttl)
    }

    fun read(articleId: Long): Long = redisTemplate.opsForValue()
        .get(generateKey(articleId))
        ?.toLong() ?: 0L

    private fun generateKey(articleId: Long) = KEY_FORMAT.format(articleId)

    companion object{
        //hot-article::article::{articleId}::view-count
        private const val KEY_FORMAT = "hot-article::article::%s::view-count"
    }
}
