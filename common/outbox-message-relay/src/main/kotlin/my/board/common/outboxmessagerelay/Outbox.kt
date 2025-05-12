package my.board.common.outboxmessagerelay

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import my.board.common.event.EventType
import java.time.LocalDateTime

@Table(name = "outbox")
@Entity
class Outbox(
    @Id
    val outboxId: Long,
    @Enumerated(EnumType.STRING)
    val eventType: EventType,
    val payload: String,
    val shardKey: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(
            outboxId: Long,
            eventType: EventType,
            payload: String,
            shardKey: Long,
        ) = Outbox(
            outboxId = outboxId,
            eventType = eventType,
            payload = payload,
            shardKey = shardKey,
            createdAt = LocalDateTime.now()
        )
    }
}
