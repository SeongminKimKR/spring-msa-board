package my.board.comment.service.response

import my.board.comment.entity.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment) = CommentResponse(
            commentId = comment.commentId,
            content = comment.content,
            parentCommentId = comment.parentCommentId,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            createdAt = comment.createdAt
        )
    }
}
