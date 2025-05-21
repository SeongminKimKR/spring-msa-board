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
class ArticleDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
) : EventHandler<ArticleCreatedEventPayload>{
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        // 게시글 목록을 먼저 지워야 목록에 없는 데이터를 조회하는 상황 예방 (극히 드뭄)
        articleIdListRepository.delete(payload.boardId, payload.articleId)
        articleQueryModelRepository.delete(payload.articleId)
        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>) = EventType.ARTICLE_DELETED == event.type
}
