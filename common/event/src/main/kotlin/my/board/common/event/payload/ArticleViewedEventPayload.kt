package my.board.common.event.payload

import my.board.common.event.EventPayload

data class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long,
): EventPayload
