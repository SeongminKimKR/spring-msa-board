package my.board.comment.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import my.board.comment.service.request.CommentCreateRequestV2
import java.time.LocalDateTime

@Table(name = "comment_v2")
@Entity
class CommentV2(
    @Id
    val commentId: Long,
    val content: String,
    @Embedded
    val commentPath: CommentPath,
    val articleId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    var deleted: Boolean = false
        protected set

    fun isRoot() = commentPath.isRoot() ?: false

    fun delete() {
        deleted = true
    }

    companion object {
        fun from(
            id: Long,
            parentCommentPath: CommentPath,
            request: CommentCreateRequestV2,
        )= CommentV2 (
            commentId = id,
            content = request.content,
            articleId = request.articleId,
            writerId = request.writerId,
            commentPath = parentCommentPath
        )
    }
}

