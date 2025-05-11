package my.board.common.event

import my.board.common.dataserializer.DataSerializer.deserialize
import my.board.common.dataserializer.DataSerializer.serialize


class Event<T : EventPayload>(
    val eventId: Long,
    val type: EventType,
    val payload: T,
) {
    fun toJson() = serialize(this)

    companion object {
        fun of(
            eventId: Long,
            type: EventType,
            payload: EventPayload,
        ) = Event(
            eventId = eventId,
            type = type,
            payload = payload,
        )

        fun fromJson(json: String?): Event<EventPayload>? {
            val eventRaw = deserialize(json, EventRaw::class.java)
                ?: return null

            val eventType = EventType.from(eventRaw.type)
            val payloadClass = eventType.payloadClass
            val payload = deserialize(eventRaw.payload, payloadClass)

            return of(eventRaw.eventId, eventType, payload)
        }

        data class EventRaw(
            val eventId: Long,
            val type: String,
            val payload: Any,
        )
    }
}
