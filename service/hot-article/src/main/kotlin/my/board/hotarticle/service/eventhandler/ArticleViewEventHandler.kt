package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleViewedEventPayload
import my.board.hotarticle.repository.ArticleViewCountRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleViewEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository,
) : EventHandler<ArticleViewedEventPayload> {
    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload = event.payload
        articleViewCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleViewCount,
            TimeCalculatorUtils.calculateDurationMidnight()
        )
    }

    override fun supports(event: Event<ArticleViewedEventPayload>): Boolean =
        EventType.ARTICLE_VIEWED == event.type

    override fun findArticleId(event: Event<ArticleViewedEventPayload>): Long =
        event.payload.articleId
}
