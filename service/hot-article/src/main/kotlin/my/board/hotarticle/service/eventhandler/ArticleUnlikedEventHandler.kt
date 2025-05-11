package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleUnlikedEventPayload
import my.board.hotarticle.repository.ArticleLikeCountRepository
import my.board.hotarticle.utils.TimeCalculatorUtils
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleUnlikedEventPayload> {
    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationMidnight(),
        )
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean =
        EventType.ARTICLE_UNLIKED == event.type

    override fun findArticleId(event: Event<ArticleUnlikedEventPayload>): Long =
        event.payload.articleId
}
