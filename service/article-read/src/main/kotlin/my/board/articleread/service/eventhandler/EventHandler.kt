package my.board.articleread.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
}
