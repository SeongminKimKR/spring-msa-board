package my.board.comment.repository

import my.board.comment.entity.ArticleCommentCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleCommentCountRepository : JpaRepository<ArticleCommentCount, Long> {
    @Query(
        value = "update article_comment_count set comment_count = comment_count + 1 where article_id = :articleId",
        nativeQuery = true
    )
    @Modifying
    fun increase(
        @Param("articleId") articleId: Long,
    ): Int

    @Query(
        value = "update article_comment_count set comment_count = comment_count - 1 where article_id = :articleId",
        nativeQuery = true
    )
    @Modifying
    fun decrease(
        @Param("articleId") articleId: Long,
    ): Int
}
