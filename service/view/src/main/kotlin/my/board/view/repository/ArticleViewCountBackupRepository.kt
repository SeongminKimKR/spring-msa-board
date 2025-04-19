package my.board.view.repository

import my.board.view.entitiy.ArticleViewCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleViewCountBackupRepository: JpaRepository<ArticleViewCount, Long>{
    @Query(
        value = "update article_view_count set view_count = :viewCount " +
                "where article_id = :articleId and view_count < :viewCount",
        nativeQuery = true
    )
    @Modifying
    fun updateViewCount(
        @Param("articleId") articleId: Long,
        @Param("viewCount") viewCount: Long,
    ): Int
}
