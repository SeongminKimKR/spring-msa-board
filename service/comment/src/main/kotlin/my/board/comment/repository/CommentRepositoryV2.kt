package my.board.comment.repository

import my.board.comment.entity.Comment
import my.board.comment.entity.CommentV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CommentRepositoryV2 : JpaRepository<CommentV2, Long> {
    @Query("select c from CommentV2 c where c.commentPath.path = :path")
    fun findByPath(@Param("path") path: String): Optional<CommentV2>


    @Query(
        value = "select path from comment_v2 " +
                "where article_id = :articleId and path > :pathPrefix and path like :pathPrefix% " +
                "order by path desc limit 1",
        nativeQuery = true
    )
    fun findDescendantTopPath(
        @Param("articleId") articleId: Long,
        @Param("pathPrefix") pathPrefix: String,
    ): Optional<String>

    @Query(
        value = "select comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, " +
                "comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at " +
                "from (" +
                "   select comment_id from comment_v2 where article_id = :articleId " +
                "   order by path asc " +
                "   limit :limit offset :offset" +
                ") t left join comment_v2 on t.comment_id = comment_v2.comment_id",
        nativeQuery = true,
    )
    fun findAll(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long,
    ): List<CommentV2>

    @Query(
        value = "select count(*) from (" +
                " select comment_id from comment_v2 where article_id = :articleId limit :limit " +
                ") t",
        nativeQuery = true
    )
    fun count(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = "select comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, " +
                "comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at " +
                "from comment_v2 " +
                "where article_id = :articleId " +
                "order by path asc " +
                "limit :limit",
        nativeQuery = true,
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): List<CommentV2>

    @Query(
        value = "select comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id, " +
                "comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at " +
                "from comment_v2 " +
                "where article_id = :articleId and path > :lastPath " +
                "order by path asc " +
                "limit :limit",
        nativeQuery = true,
    )
    fun findAllInfiniteScroll(
        @Param("articleId") articleId: Long,
        @Param("lastPath") lastPath: String,
        @Param("limit") limit: Long,
    ): List<CommentV2>
}
