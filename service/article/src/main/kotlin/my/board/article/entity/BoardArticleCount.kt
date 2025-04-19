package my.board.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Table(name = "board_article_count")
@Entity
class BoardArticleCount(
    @Id
    val boardId: Long,
    articleCount: Long = 0L,
) {
    var articleCount: Long = articleCount
        protected set
}
