package my.board.article.repository

import my.board.article.entity.BoardArticleCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardArticleCountRepository:JpaRepository<BoardArticleCount, Long> {
    @Query(
        value = "update board_article_count set article_count = article_count + 1 where board_id = :boardId",
        nativeQuery = true
    )
    @Modifying
    fun increase(
        @Param("boardId") boardId: Long,
    ): Int

    @Query(
        value = "update board_article_count set article_count = article_count - 1 where board_id = :boardId",
        nativeQuery = true
    )
    @Modifying
    fun decrease(
        @Param("boardId") boardId: Long,
    ): Int
}
