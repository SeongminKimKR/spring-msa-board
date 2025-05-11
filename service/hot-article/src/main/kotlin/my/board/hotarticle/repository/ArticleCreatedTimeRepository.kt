package my.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ArticleCreatedTimeRepository (
    private val redisTemplate: StringRedisTemplate,
){
    fun createOrUpdate(
        articleId: Long,
        createdAt: LocalDateTime,
        ttl: Duration
    ) {
        redisTemplate.opsForValue()
            .set(
                generateKey(articleId),
                createdAt.toInstant(ZoneOffset.UTC)
                    .toEpochMilli()
                    .toString(),
                ttl,
            )
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? = redisTemplate.opsForValue()
        .get(generateKey(articleId))
        ?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it.toLong()), ZoneOffset.UTC)
        }

    private fun generateKey(articleId: Long) = KEY_FORMAT.format(articleId)

    companion object{
        //hot-article::article::{articleId}::created-time
        private const val KEY_FORMAT = "hot-article::article::%s::created-time"
    }
}
