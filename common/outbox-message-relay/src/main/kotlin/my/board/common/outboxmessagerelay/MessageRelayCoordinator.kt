package my.board.common.outboxmessagerelay

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class MessageRelayCoordinator(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${spring.application.name}")
    private val applicationName: String,
) {
    fun assignedShards(): AssignedShard {
        return AssignedShard.of(APP_ID, findAppIds(), MessageRelayConstants.SHARD_COUNT)
    }

    private fun findAppIds(): List<String> =
        redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)
            ?.toList()
            ?.sorted() ?: emptyList()

    private fun generateKey(): String = "message-relay-coordinator::app-list::%s".format(applicationName)

    @Scheduled(fixedDelay = Companion.PING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined(RedisCallback<Any> { action ->
            val conn = action as StringRedisConnection
            val key = generateKey()

            conn.zAdd(key, Instant.now().toEpochMilli().toDouble(), APP_ID)
            conn.zRemRange(
                key,
                Double.NEGATIVE_INFINITY.toLong(),
                Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli()
            )
            null
        } as RedisCallback<*>)
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID)
    }

    companion object {
        private val APP_ID = UUID.randomUUID().toString()
        private const val PING_INTERVAL_SECONDS = 3L
        private const val PING_FAILURE_THRESHOLD = 3
    }
}
