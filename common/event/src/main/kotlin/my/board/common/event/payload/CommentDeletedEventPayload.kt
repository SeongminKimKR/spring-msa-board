package my.board.common.event.payload

import my.board.common.event.EventPayload
import java.time.LocalDateTime

data class CommentDeletedEventPayload(
    val commentId: Long,
    val content: String,
    val path: String,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val articleCommentCount: Long
): EventPayload
