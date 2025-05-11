package my.board.hotarticle.service

import io.kotest.core.spec.style.FunSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import my.board.common.event.Event
import my.board.common.event.EventPayload
import my.board.hotarticle.repository.ArticleCreatedTimeRepository
import my.board.hotarticle.repository.HotArticleListRepository
import my.board.hotarticle.service.eventhandler.EventHandler
import java.time.LocalDateTime

class HotArticleScoreUpdaterTest : FunSpec({
    val hotArticleScoreCalculator = mockk<HotArticleScoreCalculator>()
    val hotArticleListRepository = mockk<HotArticleListRepository>()
    val articleCreatedTimeRepository = mockk<ArticleCreatedTimeRepository>()
    val hotArticleScoreUpdater = HotArticleScoreUpdater(
        hotArticleListRepository,
        hotArticleScoreCalculator,
        articleCreatedTimeRepository
    )

    test("updateIfArticleNotCreatedToday") {
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()

        every { eventHandler.findArticleId(event) } returns articleId

        val createdTime = LocalDateTime.now().minusDays(1)

        every { articleCreatedTimeRepository.read(articleId) } returns createdTime

        hotArticleScoreUpdater.update(event, eventHandler)

        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleListRepository.add(any(), any(), any(), any(), any()) }
    }

    test("updateIfArticleCreatedToday") {
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()

        every { eventHandler.findArticleId(event) } returns articleId

        val createdTime = LocalDateTime.now()

        every { articleCreatedTimeRepository.read(articleId) } returns createdTime
        every { eventHandler.handle(event) } just Runs
        every { hotArticleListRepository.add(any(), any(), any(), any(), any()) } just Runs
        every { hotArticleScoreCalculator.calculate(articleId)} returns 1L

        hotArticleScoreUpdater.update(event, eventHandler)

        verify(exactly = 1) { eventHandler.handle(event) }
        verify(exactly = 1) { hotArticleListRepository.add(any(), any(), any(), any(), any()) }
    }
})
