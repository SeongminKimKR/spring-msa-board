package my.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/***
 * 좋아요, 댓글, 조회 이벤트가 발생했을 때 게시글이 오늘 게시글인지 확인하려면, 게시글 서비스에 조회가 필요
 * 하지만 게시글 생성 시간을 저장하고 있으면, 오늘 게시글인지 게시글 서비스에 조회하지 않고 여기에서 조회하면 됨
 */
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
