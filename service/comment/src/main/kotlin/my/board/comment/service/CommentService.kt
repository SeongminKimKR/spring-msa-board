package my.board.comment.service

import my.board.comment.entity.Comment
import my.board.comment.repository.CommentRepository
import my.board.comment.service.request.CommentCreateRequest
import my.board.comment.service.response.CommentResponse
import my.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate.not

@Service
class CommentService(
    private val commentRepository: CommentRepository,
) {
    @Transactional
    fun create(
        request: CommentCreateRequest,
    ): CommentResponse {
        val parent = findParent(request)
        val comment = commentRepository.save(Comment.from(Snowflake.nextId(), parent?.commentId, request))

        return CommentResponse.from(comment)
    }

    fun read(commentId: Long): CommentResponse {
        return CommentResponse.from(
            commentRepository.findById(commentId).orElseThrow()
        )
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            .filter(not(Comment::deleted))
            .ifPresent { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }
            }
    }

    private fun delete(comment: Comment) {
        commentRepository.delete(comment)

        if(!comment.isRoot()) {
            commentRepository.findById(comment.parentCommentId)
                .filter(Comment::deleted)
                .filter(not(this::hasChildren))
                .ifPresent(this::delete)
        }
    }

    private fun hasChildren(comment: Comment): Boolean =
        commentRepository.countBy(comment.articleId, comment.commentId, 2L) == 2L

    private fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId ?: return null

        return commentRepository.findById(parentCommentId)
            .filter { !it.deleted }
            .filter(Comment::isRoot)
            .orElseThrow()
    }
}
