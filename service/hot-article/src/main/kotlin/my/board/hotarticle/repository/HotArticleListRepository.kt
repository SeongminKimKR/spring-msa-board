package my.board.hotarticle.repository

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotArticleListRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    private val logger = LoggerFactory.getLogger(HotArticleListRepository::class.java)
    fun add(
        articleId: Long,
        time: LocalDateTime,
        score: Long,
        limit: Long,
        ttl: Duration,
    ) {
        redisTemplate.executePipelined(RedisCallback<Any> { action ->
            val conn = action as StringRedisConnection
            val key = generateKey(time)
            conn.zAdd(key, score.toDouble(), articleId.toString())
            conn.zRemRange(key, 0, -limit - 1)
            conn.expire(key, ttl.toSeconds())
            null
        } as RedisCallback<*>)
    }

    fun readAll(dateStr: String): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(generateKey(dateStr), 0, -1)
            ?.onEach { tuple ->
                logger.info("[HotArticleListRepository.readAll] articleId={}, score={}", tuple.value, tuple.score)
            }
            ?.mapNotNull { it.value?.toString()?.toLongOrNull() }
            ?: emptyList()
    }

    fun remove(
        articleId: Long,
        time: LocalDateTime
    ) {
        redisTemplate.opsForZSet()
            .remove(generateKey(time), articleId.toString())
    }

    private fun generateKey(time: LocalDateTime): String =
        generateKey(TIME_FORMATTER.format(time))

    private fun generateKey(dateStr: String): String = KEY_FORMAT.format(dateStr)

    companion object {
        // hot-article::list::{yyyyMMdd}
        private const val KEY_FORMAT = "hot-article::list::%s"
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}
