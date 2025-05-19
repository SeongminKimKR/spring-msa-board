package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CommentCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<CommentCreatedEventPayload>{
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)?.let { articleQueryModel ->
            articleQueryModel.updateBy(event.payload)
            articleQueryModelRepository.update(articleQueryModel)
        }
    }

    override fun supports(event: Event<CommentCreatedEventPayload>) = EventType.COMMENT_CREATED == event.type
}
