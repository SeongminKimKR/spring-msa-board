package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.CommentDeletedEventPayload
import my.board.hotarticle.repository.ArticleCommentCountRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentDeletedEventPayload> {
    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationMidnight()
        )
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean =
        EventType.COMMENT_DELETED == event.type

    override fun findArticleId(event: Event<CommentDeletedEventPayload>): Long =
        event.payload.articleId
}
