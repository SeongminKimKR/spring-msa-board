package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleLikedEventPayload
import my.board.hotarticle.repository.ArticleLikeCountRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleLikedEventPayload> {
    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationMidnight(),
        )
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean =
        EventType.ARTICLE_LIKED == event.type

    override fun findArticleId(event: Event<ArticleLikedEventPayload>): Long =
        event.payload.articleId
}
