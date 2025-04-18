package my.board.comment.service

import my.board.comment.entity.Comment
import my.board.comment.entity.CommentPath
import my.board.comment.entity.CommentV2
import my.board.comment.repository.CommentRepositoryV2
import my.board.comment.service.request.CommentCreateRequestV2
import my.board.comment.service.response.CommentPageResponseV2
import my.board.comment.service.response.CommentResponse
import my.board.comment.service.response.CommentResponseV2
import my.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate
import java.util.function.Predicate.not

@Service
class CommentServiceV2(
    private val commentRepository: CommentRepositoryV2,
) {

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponseV2 {
        val parent = findParent(request)
        val parentCommentPath = parent?.commentPath ?: CommentPath.from("")
        val comment = commentRepository.save(
            CommentV2.from(
                Snowflake.nextId(),
                parentCommentPath.createChildCommentPath(
                    commentRepository.findDescendantTopPath(request.articleId, parentCommentPath.path)
                        .orElse(null)
                ),
                request
            )
        )

        return CommentResponseV2.from(comment)
    }

    fun read(commentId: Long): CommentResponseV2 = CommentResponseV2.from(
        commentRepository.findById(commentId)
            .orElseThrow()
    )

    @Transactional
    fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            .filter(not(CommentV2::deleted))
            .ifPresent { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }

            }
    }

    fun readAll(
        articleId: Long,
        page: Long,
        pageSize: Long,
    ): CommentPageResponseV2 = CommentPageResponseV2(
        comments = commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize)
            .map(CommentResponseV2::from),
        commentCount = commentRepository.count(
            articleId,
            PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
        )
    )

    fun readAllInfiniteScroll(
        articleId: Long,
        lastPath: String?,
        pageSize: Long,
    ): List<CommentResponseV2> = lastPath?.let {
        commentRepository.findAllInfiniteScroll(articleId,lastPath,pageSize)
            .map(CommentResponseV2::from)
    } ?: commentRepository.findAllInfiniteScroll(articleId, pageSize)
        .map(CommentResponseV2::from)

    private fun hasChildren(comment: CommentV2): Boolean {
        return commentRepository.findDescendantTopPath(
            comment.articleId,
            comment.commentPath.path
        ).isPresent
    }

    private fun delete(comment: CommentV2) {
        commentRepository.delete(comment)

        if (!comment.isRoot()) {
            commentRepository.findByPath(comment.commentPath.getParentPath())
                .filter(CommentV2::deleted)
                .filter(not(this::hasChildren))
                .ifPresent(this::delete)
        }
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        return request.parentPath?.let {
            commentRepository.findByPath(request.parentPath)
                .filter(not(CommentV2::deleted))
                .orElseThrow()
        }
    }
}
