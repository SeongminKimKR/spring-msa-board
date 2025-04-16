package my.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import my.board.comment.service.request.CommentCreateRequest
import java.time.LocalDateTime

@Table(name = "comment")
@Entity
class Comment (
    @Id
    val commentId: Long,
    val content: String,
    parentCommentId: Long?,
    val articleId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val parentCommentId: Long = parentCommentId ?: commentId

    var deleted: Boolean = false
        protected set

    fun isRoot() = parentCommentId == commentId

    fun delete() {
        deleted = true
    }

    companion object {
        fun from(
            id: Long ,
            parentCommentId: Long?,
            request: CommentCreateRequest,
        )= Comment (
            commentId = id,
            content = request.content,
            parentCommentId = parentCommentId ?: id,
            articleId = request.articleId,
            writerId = request.writerId,
        )
    }
}
