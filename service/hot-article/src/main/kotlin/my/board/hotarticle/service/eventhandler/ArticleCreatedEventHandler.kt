package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.hotarticle.repository.ArticleCreatedTimeRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.createOrUpdate(
            payload.articleId,
            payload.createdAt,
            TimeCalculatorUtils.calculateDurationMidnight()
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean =
        EventType.ARTICLE_CREATED == event.type

    override fun findArticleId(event: Event<ArticleCreatedEventPayload>): Long =
        event.payload.articleId
}
