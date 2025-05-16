package my.board.hotarticle.service

import io.kotest.core.spec.style.FunSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import my.board.common.event.Event
import my.board.common.event.EventPayload
import my.board.common.event.EventType
import my.board.hotarticle.client.ArticleClient
import my.board.hotarticle.repository.HotArticleListRepository
import my.board.hotarticle.service.eventhandler.EventHandler

class HotArticleServiceTest : FunSpec({
    val articleClient =  mockk<ArticleClient>()
    val eventHandler = mockk<EventHandler<EventPayload>>()
    val eventHandlers = listOf(eventHandler)
    val hotArticleScoreUpdater = mockk<HotArticleScoreUpdater>()
    val hotArticleListRepository =  mockk<HotArticleListRepository>()
    val hotArticleService = HotArticleService(
        articleClient,
        eventHandlers,
        hotArticleScoreUpdater,
        hotArticleListRepository,
    )

    test("handleEventIfEventHandlerNotFoundTest") {
        val event = mockk<Event<EventPayload>>()

        every { eventHandler.supports(event) } returns false

        hotArticleService.handleEvent(event)

        verify(exactly = 0){ eventHandler.handle(event) }
        verify(exactly = 0){ hotArticleScoreUpdater.update(event, eventHandler) }
    }

    test("handleEventIfArticleCreatedEvent") {
        val event = mockk<Event<EventPayload>>()

        every { event.type } returns EventType.ARTICLE_CREATED
        every { eventHandler.supports(event) } returns true
        every { eventHandler.handle(event) } just Runs

        hotArticleService.handleEvent(event)

        verify(exactly = 1){ eventHandler.handle(event) }
        verify(exactly = 0){ hotArticleScoreUpdater.update(event, eventHandler) }
    }

    test("handleEventIfArticleDeletedEvent") {
        val event = mockk<Event<EventPayload>>()

        every { event.type } returns EventType.ARTICLE_DELETED
        every { eventHandler.supports(event) } returns true
        every { eventHandler.handle(event) } just Runs

        hotArticleService.handleEvent(event)

        verify(exactly = 1){ eventHandler.handle(event) }
        verify(exactly = 0){ hotArticleScoreUpdater.update(event, eventHandler) }
    }

    test("handleEventIfScoreUpdatableEvent") {
        val event = mockk<Event<EventPayload>>()

        every { event.type } returns mockk<EventType>()
        every { eventHandler.supports(event) } returns true
        every { hotArticleScoreUpdater.update(event, eventHandler) } just Runs

        hotArticleService.handleEvent(event)

        verify(exactly = 0){ eventHandler.handle(event) }
        verify(exactly = 1){ hotArticleScoreUpdater.update(event, eventHandler) }
    }
})
