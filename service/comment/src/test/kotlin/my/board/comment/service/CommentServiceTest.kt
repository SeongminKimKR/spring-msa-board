package my.board.comment.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import my.board.comment.entity.Comment
import my.board.comment.repository.CommentRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

class CommentServiceTest : FunSpec() {
    val commentRepository = mockk<CommentRepository>()
    val commentService = CommentService(commentRepository)

    private fun createComment(articleId: Long, commentId: Long): Comment {
        val comment = mockk<Comment>()
        every { comment.articleId } returns articleId
        every { comment.commentId } returns commentId
        return comment
    }

    private fun createComment(articleId: Long, commentId: Long, parentCommentId: Long): Comment {
        val comment = createComment(articleId, commentId)
        every { comment.parentCommentId } returns parentCommentId
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
    }
}
