package my.board.view.entitiy

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_view_count")
@Entity
class ArticleViewCount(
    @Id
    val articleId: Long,
    viewCount: Long = 0L,
) {
    var viewCount: Long = viewCount
        protected set
}
