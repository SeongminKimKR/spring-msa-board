package my.board.articleread.repository

import my.board.articleread.client.ArticleClient
import my.board.common.event.payload.ArticleCreatedEventPayload
import my.board.common.event.payload.ArticleLikedEventPayload
import my.board.common.event.payload.ArticleUnlikedEventPayload
import my.board.common.event.payload.ArticleUpdatedEventPayload
import my.board.common.event.payload.CommentCreatedEventPayload
import my.board.common.event.payload.CommentDeletedEventPayload
import java.time.LocalDateTime

class ArticleQueryModel(
    val articleId: Long,
    title: String,
    content: String,
    boardId: Long,
    writerId: Long,
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
    articleCommentCount: Long = 0L,
    articleLikeCount: Long = 0L,
) {
    var title: String = title
        protected set
    var content: String = content
        protected set
    var boardId: Long = boardId
        protected set
    var writerId: Long = writerId
        protected set
    var createdAt: LocalDateTime = createdAt
        protected set
    var modifiedAt: LocalDateTime = modifiedAt
        protected set
    var articleCommentCount: Long = articleCommentCount
        protected set
    var articleLikeCount: Long = articleLikeCount
        protected set

    fun updateBy(payLoad: CommentCreatedEventPayload) {
        articleCommentCount = payLoad.articleCommentCount
    }

    fun updateBy(payLoad: CommentDeletedEventPayload) {
        articleCommentCount = payLoad.articleCommentCount
    }

    fun updateBy(payLoad: ArticleLikedEventPayload) {
        articleLikeCount = payLoad.articleLikeCount
    }

    fun updateBy(payLoad: ArticleUnlikedEventPayload) {
        articleLikeCount = payLoad.articleLikeCount
    }

    fun updateBy(payload: ArticleUpdatedEventPayload) {
        title = payload.title
        content = payload.content
        boardId = payload.boardId
        writerId = payload.writerId
        createdAt = payload.createdAt
        modifiedAt = payload.modifiedAt
    }

    companion object {
        fun create(payload: ArticleCreatedEventPayload) = ArticleQueryModel(
            articleId = payload.articleId,
            title = payload.title,
            content = payload.content,
            boardId = payload.boardId,
            writerId = payload.writerId,
            createdAt = payload.createdAt,
            modifiedAt = payload.modifiedAt,
        )

        fun create(
            article: ArticleClient.Companion.ArticleResponse,
            commentCount: Long,
            likeCount: Long,
        ) = ArticleQueryModel(
            articleId = article.articleId,
            title = article.title,
            content = article.content,
            boardId = article.articleId,
            writerId = article.writerId,
            createdAt = article.createdAt,
            modifiedAt = article.modifiedAt,
            articleCommentCount = commentCount,
            articleLikeCount = likeCount,
        )
    }
}
