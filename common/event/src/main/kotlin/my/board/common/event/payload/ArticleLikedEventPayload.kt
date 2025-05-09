package my.board.common.event.payload

import my.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val articleLikeCount: Long,
): EventPayload
