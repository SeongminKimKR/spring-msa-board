package my.board.hotarticle.service

import my.board.common.event.Event
import my.board.common.event.EventPayload
import my.board.hotarticle.repository.ArticleCreatedTimeRepository
import my.board.hotarticle.repository.HotArticleListRepository
import my.board.hotarticle.service.eventhandler.EventHandler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleListRepository: HotArticleListRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) {
    fun update(
        event: Event<EventPayload>,
        eventHandler: EventHandler<EventPayload>,
    ) {
        val articleId = eventHandler.findArticleId(event)
        val createdTime = articleCreatedTimeRepository.read(articleId)

        if(!isArticleCreatedToday(createdTime)) {
            return
        }

        eventHandler.handle(event)

        val score = hotArticleScoreCalculator.calculate(articleId)

        hotArticleListRepository.add(
            articleId,
            createdTime!!,
            score,
            HOT_ARTICLE_COUNT,
            HOT_ARTICLE_TTL
        )
    }

    private fun isArticleCreatedToday(createdTime: LocalDateTime?): Boolean =
        createdTime != null && createdTime.toLocalDate().equals(LocalDate.now())

    companion object {
        private const val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL = Duration.ofDays(10)
    }
}
