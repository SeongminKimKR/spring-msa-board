package my.board.common.event.payload

import my.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleDeletedEventPayload(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val boardArticleCount: Long,
): EventPayload
