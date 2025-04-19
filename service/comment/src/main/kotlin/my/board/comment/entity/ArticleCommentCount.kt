package my.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_comment_count")
@Entity
class ArticleCommentCount (
    @Id
    val articleId: Long,
    commentCount: Long = 0L
){

    var commentCount: Long = commentCount
        protected set
}
