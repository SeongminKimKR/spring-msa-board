package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleCreatedEventPayload>{
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.create(payload),
            Duration.ofDays(1)
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>) = EventType.ARTICLE_CREATED == event.type
}
