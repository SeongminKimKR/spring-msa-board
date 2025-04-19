package my.board.like.repository

import my.board.like.entity.ArticleLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ArticleLikeRepository: JpaRepository<ArticleLike, Long> {
    fun findByArticleIdAndUserId(
        articleId: Long,
        userId: Long,
    ): Optional<ArticleLike>
}
