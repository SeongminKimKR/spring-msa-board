package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleCreatedEventPayload>{
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.delete(payload.articleId)
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>) = EventType.ARTICLE_DELETED == event.type
}
