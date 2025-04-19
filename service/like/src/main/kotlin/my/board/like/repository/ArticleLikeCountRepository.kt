package my.board.like.repository

import jakarta.persistence.LockModeType
import my.board.like.entity.ArticleLikeCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ArticleLikeCountRepository : JpaRepository<ArticleLikeCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedByArticleId(articleId: Long): Optional<ArticleLikeCount>

    @Query(
        value = "update article_like_count set like_count = like_count + 1 where article_id = :articleId",
        nativeQuery = true
    )
    @Modifying
    fun increase(
        @Param("articleId") articleId: Long,
    ): Int

    @Query(
        value = "update article_like_count set like_count = like_count - 1 where article_id = :articleId",
        nativeQuery = true
    )
    @Modifying
    fun decrease(
        @Param("articleId") articleId: Long,
    ): Int
}
