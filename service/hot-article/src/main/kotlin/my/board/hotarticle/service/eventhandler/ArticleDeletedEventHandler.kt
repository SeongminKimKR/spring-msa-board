package my.board.hotarticle.service.eventhandler

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleDeletedEventPayload
import my.board.hotarticle.repository.ArticleCreatedTimeRepository
import my.board.hotarticle.repository.HotArticleListRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val articleListRepository: HotArticleListRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleDeletedEventPayload>{
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.delete(payload.articleId)
        articleListRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean =
        EventType.ARTICLE_DELETED == event.type

    override fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long =
        event.payload.articleId
}
