package my.board.common.outboxmessagerelay

import my.board.common.event.Event
import my.board.common.event.EventPayload
import my.board.common.event.EventType
import my.board.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisher(
   private val applicationEventPublisher: ApplicationEventPublisher,
) {
    private val outboxIdSnowflake = Snowflake()
    private val eventIdSnowflake = Snowflake()

    fun publish(
        type: EventType,
        payload: EventPayload,
        shardKey: Long
    ) {
        val payload = Event.of(
            eventIdSnowflake.nextId(), type, payload
        ).toJson() ?: throw IllegalStateException("serialize fail")

        // articleId = 10, shardKey == articleId
        // 10 % 4 = 물리적 샤드 2
        val outbox = Outbox.create(
            outboxIdSnowflake.nextId(),
            type,
            payload,
            shardKey % MessageRelayConstants.SHARD_COUNT
        )

        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox))
    }
}
