package my.board.hotarticle.service.response

import my.board.hotarticle.client.ArticleClient
import java.time.LocalDateTime

data class HotArticleResponse(
    private val articleId: Long,
    private val title: String,
    private val createdAt: LocalDateTime
) {
    companion object {
        fun from(articleResponse: ArticleClient.Companion.ArticleResponse)= HotArticleResponse(
            articleId = articleResponse.articleId,
            title = articleResponse.title,
            createdAt = articleResponse.createdAt
        )
    }
}
