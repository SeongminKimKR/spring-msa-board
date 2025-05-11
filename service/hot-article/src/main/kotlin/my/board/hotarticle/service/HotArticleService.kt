package my.board.hotarticle.service

import my.board.common.event.Event
import my.board.common.event.EventPayload
import my.board.common.event.EventType
import my.board.hotarticle.client.ArticleClient
import my.board.hotarticle.repository.HotArticleListRepository
import my.board.hotarticle.service.eventhandler.EventHandler
import my.board.hotarticle.service.response.HotArticleResponse
import org.springframework.stereotype.Service


@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository,
) {
    fun handleEvent(
        event: Event<EventPayload>,
    ) {
        val eventHandler = findEventHandler(event)
            ?: return

        if(isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, eventHandler)
        }
    }

    fun readAll(dateStr: String): List<HotArticleResponse> {
        return hotArticleListRepository.readAll(dateStr)
            .mapNotNull(articleClient::read)
            .map(HotArticleResponse::from)
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<EventPayload>? =
        eventHandlers.firstOrNull { eventHandler -> eventHandler.supports(event) }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean =
        EventType.ARTICLE_CREATED == event.type || EventType.ARTICLE_DELETED == event.type
}

