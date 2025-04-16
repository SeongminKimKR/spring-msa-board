package my.board.comment.service

import io.kotest.core.spec.style.FunSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import my.board.comment.entity.Comment
import my.board.comment.repository.CommentRepository
import java.util.*

class CommentServiceTest : FunSpec() {
    val commentRepository = mockk<CommentRepository>()
    val commentService = CommentService(commentRepository)

    private fun createComment(articleId: Long, commentId: Long): Comment {
        val comment = mockk<Comment>()
        every { comment.articleId } returns articleId
        every { comment.commentId } returns commentId
        every { comment.deleted } returns false
        every { comment.delete() } just Runs
        return comment
    }

    private fun createComment(articleId: Long, commentId: Long, parentCommentId: Long): Comment {
        val comment = createComment(articleId, commentId)
        every { comment.parentCommentId } returns parentCommentId
        every { comment.deleted } returns false
        every { comment.delete() } just Runs
        return comment
    }

    init {

        test("학제할 댓글이 자식이 있으면, 삭제 표시만 한다.")
        {
            val articleId = 1L
            val commentId = 2L

            val comment = createComment(articleId, commentId)

            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { commentRepository.countBy(articleId, commentId, 2L) } returns 2L


            commentService.delete(commentId)
            verify(exactly = 1) { comment.delete() }
        }

        test("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
        {
            val articleId = 1L
            val commentId = 2L
            val parentCommentId = 1L

            val comment = createComment(articleId, commentId, parentCommentId)
            val parentComment = mockk<Comment>()

            every { parentComment.deleted } returns false
            every { comment.isRoot() } returns false
            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { commentRepository.countBy(articleId, commentId, 2L) } returns 1L
            every { commentRepository.findById(parentCommentId) } returns Optional.of(parentComment)
            every { commentRepository.delete(comment) } just Runs

            commentService.delete(commentId)

            verify(exactly = 1) { commentRepository.delete(comment) }
            verify(exactly = 0) { commentRepository.delete(parentComment) }
        }

        test("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다.")
        {
            val articleId = 1L
            val commentId = 2L
            val parentCommentId = 1L

            val comment = createComment(articleId, commentId, parentCommentId)
            val parentComment = createComment(articleId, parentCommentId)

            every { parentComment.deleted } returns true
            every { parentComment.isRoot() } returns true
            every { comment.isRoot() } returns false
            every { commentRepository.findById(commentId) } returns Optional.of(comment)
            every { commentRepository.countBy(articleId, commentId, 2L) } returns 1L
            every { commentRepository.findById(parentCommentId) } returns Optional.of(parentComment)
            every { commentRepository.countBy(articleId, parentCommentId, 2L) } returns 1L
            every { commentRepository.delete(comment) } just Runs
            every { commentRepository.delete(parentComment) } just Runs

            commentService.delete(commentId)

            verify(exactly = 1) { commentRepository.delete(comment) }
            verify(exactly = 1) { commentRepository.delete(parentComment) }
        }
    }
}
