package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventPayload


interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
    fun findArticleId(event: Event<T>): Long
}
