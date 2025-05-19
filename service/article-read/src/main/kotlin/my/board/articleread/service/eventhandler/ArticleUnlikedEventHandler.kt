package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.ArticleLikedEventPayload
import my.board.common.event.payload.ArticleUnlikedEventPayload
import my.board.common.event.payload.CommentCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleUnlikedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleUnlikedEventPayload>{
    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)?.let { articleQueryModel ->
            articleQueryModel.updateBy(event.payload)
            articleQueryModelRepository.update(articleQueryModel)
        }
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>) = EventType.ARTICLE_UNLIKED == event.type
}
