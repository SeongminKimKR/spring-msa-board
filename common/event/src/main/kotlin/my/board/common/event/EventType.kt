package my.board.common.event

import my.board.common.event.EventType.Topic.MY_BOARD_ARTICLE
import my.board.common.event.EventType.Topic.MY_BOARD_COMMENT
import my.board.common.event.EventType.Topic.MY_BOARD_LIKE
import my.board.common.event.EventType.Topic.MY_BOARD_VIEW
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.ArticleDeletedEventPayload
import my.board.common.event.payload.ArticleLikedEventPayload
import my.board.common.event.payload.ArticleUnlikedEventPayload
import my.board.common.event.payload.ArticleUpdatedEventPayload
import my.board.common.event.payload.ArticleViewedEventPayload
import my.board.common.event.payload.CommentCreatedEventPayload
import my.board.common.event.payload.CommentDeletedEventPayload
import org.slf4j.LoggerFactory

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String,
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, MY_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, MY_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, MY_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, MY_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, MY_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, MY_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, MY_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, MY_BOARD_VIEW),
    ;


    companion object {
        private val logger = LoggerFactory.getLogger(EventType::class.java)

        fun from(type: String): EventType {
            try {
                return valueOf(type)
            } catch (e: Exception) {
                logger.error("[EventType.from] type={}", type, e)
                throw IllegalArgumentException("Invalid type=$type")
            }
        }
    }

    object Topic {
        const val MY_BOARD_ARTICLE: String = "my-board-article"
        const val MY_BOARD_COMMENT: String = "my-board-comment"
        const val MY_BOARD_LIKE: String = "my-board-like"
        const val MY_BOARD_VIEW: String = "my-board-view"
    }
}
