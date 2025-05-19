package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.ArticleUpdatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleUpdatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleUpdatedEventPayload>{
    override fun handle(event: Event<ArticleUpdatedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)?.let { articleQueryModel ->
            articleQueryModel.updateBy(event.payload)
            articleQueryModelRepository.update(articleQueryModel)
        }
    }

    override fun supports(event: Event<ArticleUpdatedEventPayload>) = EventType.ARTICLE_UPDATED == event.type
}
