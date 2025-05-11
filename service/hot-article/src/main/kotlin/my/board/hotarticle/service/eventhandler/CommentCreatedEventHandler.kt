package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.CommentCreatedEventPayload
import my.board.hotarticle.repository.ArticleCommentCountRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentCreatedEventPayload> {
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationMidnight()
        )
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean =
        EventType.COMMENT_CREATED == event.type

    override fun findArticleId(event: Event<CommentCreatedEventPayload>): Long =
        event.payload.articleId
}
