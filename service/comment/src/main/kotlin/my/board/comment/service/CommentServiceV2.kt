package my.board.comment.service

import my.board.comment.entity.ArticleCommentCount
import my.board.comment.entity.Comment
import my.board.comment.entity.CommentPath
import my.board.comment.entity.CommentV2
import my.board.comment.repository.ArticleCommentCountRepository
import my.board.comment.repository.CommentRepositoryV2
import my.board.comment.service.request.CommentCreateRequestV2
import my.board.comment.service.response.CommentPageResponseV2
import my.board.comment.service.response.CommentResponse
import my.board.comment.service.response.CommentResponseV2
import my.board.common.event.EventType
import my.board.common.event.payload.CommentCreatedEventPayload
import my.board.common.event.payload.CommentDeletedEventPayload
import my.board.common.outboxmessagerelay.OutboxEventPublisher
import my.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Predicate
import java.util.function.Predicate.not

@Service
class CommentServiceV2(
    private val commentRepository: CommentRepositoryV2,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponseV2 {
        val parent = findParent(request)
        val parentCommentPath = parent?.commentPath ?: CommentPath.from("")
        val comment = commentRepository.save(
            CommentV2.from(
                snowflake.nextId(),
                parentCommentPath.createChildCommentPath(
                    commentRepository.findDescendantTopPath(request.articleId, parentCommentPath.path)
                        .orElse(null)
                ),
                request
            )
        )

        val result = articleCommentCountRepository.increase(request.articleId)

        if(result == 0) {
            articleCommentCountRepository.save(ArticleCommentCount(request.articleId, 1L))
        }

        outboxEventPublisher.publish(
            EventType.COMMENT_CREATED,
            CommentCreatedEventPayload(
                commentId = comment.commentId,
                content = comment.content,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                createdAt = comment.createdAt,
                articleCommentCount = count(comment.articleId),
                path = comment.commentPath.path
            ),
            comment.articleId
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

                outboxEventPublisher.publish(
                    EventType.COMMENT_DELETED,
                    CommentDeletedEventPayload(
                        commentId = comment.commentId,
                        content = comment.content,
                        articleId = comment.articleId,
                        writerId = comment.writerId,
                        deleted = comment.deleted,
                        createdAt = comment.createdAt,
                        articleCommentCount = count(comment.articleId),
                        path = comment.commentPath.path
                    ),
                    comment.articleId
                )
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
//            count(articleId) <- 가능
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

    fun count(articleId: Long): Long = articleCommentCountRepository.findById(articleId)
        .map(ArticleCommentCount::commentCount)
        .orElse(0L)

    private fun hasChildren(comment: CommentV2): Boolean {
        return commentRepository.findDescendantTopPath(
            comment.articleId,
            comment.commentPath.path
        ).isPresent
    }

    private fun delete(comment: CommentV2) {
        commentRepository.delete(comment)
        articleCommentCountRepository.decrease(comment.articleId)

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
