package my.board.common.outboxmessagerelay

class OutboxEvent (
    val outbox: Outbox,
){
    companion object {
        fun of(outbox: Outbox) = OutboxEvent(outbox)
    }
}
