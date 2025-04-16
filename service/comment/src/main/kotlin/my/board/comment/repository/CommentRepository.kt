package my.board.comment.repository

import my.board.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    @Query(
        value = "select count(*) from(" +
                " select comment_id from comment " +
                " where article_id = :articleId and parent_comment_id = :parentCommentId " +
                " limit :limit" +
                ") t",
        nativeQuery = true
    )
    fun countBy(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                "comment.writer_id, comment.deleted, comment.created_at " +
                "from (" +
                "   select comment_id from comment where article_id = :articleId " +
                "   order by parent_comment_id asc, comment_id asc" +
                "   limit :limit offset :offset" +
                ") t left join comment on t.comment_id = comment.comment_id"
                ,
        nativeQuery = true
    )
    fun findAll(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long,
    ): List<Comment>

    @Query(
        value = "select count(*) from (" +
                "select comment_id from comment where article_id = :articleId limit :limit" +
                ") t" ,
        nativeQuery = true
    )
    fun count(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                "comment.writer_id, comment.deleted, comment.created_at " +
                "from comment " +
                "where article_id = :articleId " +
                "order by parent_comment_id asc, comment_id asc " +
                "limit :limit"
        ,
        nativeQuery = true
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): List<Comment>

    @Query(
        value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                "comment.writer_id, comment.deleted, comment.created_at " +
                "from comment " +
                "where article_id = :articleId and (" +
                "   parent_comment_id > :lastParentCommentId or" +
                "   (parent_comment_id = :lastParentCommentId and comment_id > :lastCommentId)" +
                ")" +
                "order by parent_comment_id asc, comment_id asc " +
                "limit :limit"
        ,
        nativeQuery = true
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("lastParentCommentId") lastParentCommentId: Long,
        @Param("lastCommentId") lastCommentId: Long,
        @Param("limit") limit: Long,
    ): List<Comment>
}
