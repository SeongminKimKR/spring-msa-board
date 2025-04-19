package my.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Table(name = "article_like_count")
@Entity
class ArticleLikeCount (
    @Id
    val articleId: Long,
    likeCount: Long = 0L
) {
    var likeCount: Long = likeCount
        protected set

    @Version
    var version: Long = 0L
        protected set

    fun increase() {
        likeCount++
    }

    fun decrease() {
        likeCount--
    }
}
