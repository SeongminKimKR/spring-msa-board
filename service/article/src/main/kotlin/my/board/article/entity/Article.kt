package my.board.article.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article")
@Entity
class Article(
    @Id
    val articleId: Long,
    var boardId: Long,
    val writerId: Long,
    title: String,
    content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    var title: String = title
        protected set

    var content: String = content
        protected set

    var modifiedAt: LocalDateTime = createdAt
        protected set

    fun update(
        title: String,
        content: String,
    ) {
        this.title = title
        this.content = content
        this.modifiedAt = LocalDateTime.now()
    }
}
