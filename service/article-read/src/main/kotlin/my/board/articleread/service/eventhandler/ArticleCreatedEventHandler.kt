package my.board.articleread.service.eventhandler

import my.board.articleread.repository.ArticleIdListRepository
import my.board.articleread.repository.ArticleQueryModel
import my.board.articleread.repository.ArticleQueryModelRepository
import my.board.articleread.repository.BoardArticleCountRepository
import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.common.event.payload.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
) : EventHandler<ArticleCreatedEventPayload>{
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.create(payload),
            Duration.ofDays(1)
        )

        articleIdListRepository.add(payload.boardId, payload.articleId, 1000L)
        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>) = EventType.ARTICLE_CREATED == event.type
}
