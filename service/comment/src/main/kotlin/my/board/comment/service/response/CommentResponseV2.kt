package my.board.comment.service.response

import my.board.comment.entity.Comment
import my.board.comment.entity.CommentV2
import java.time.LocalDateTime

data class CommentResponseV2(
    val commentId: Long,
    val content: String,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val path: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: CommentV2) = CommentResponseV2(
            commentId = comment.commentId,
            content = comment.content,
            path = comment.commentPath.path,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            createdAt = comment.createdAt
        )
    }
}
