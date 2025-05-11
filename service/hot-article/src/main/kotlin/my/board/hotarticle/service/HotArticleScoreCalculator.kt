package my.board.hotarticle.service

import my.board.hotarticle.repository.ArticleCommentCountRepository
import my.board.hotarticle.repository.ArticleLikeCountRepository
import my.board.hotarticle.repository.ArticleViewCountRepository
import org.springframework.stereotype.Component

@Component
class HotArticleScoreCalculator(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {
    fun calculate(articleId: Long): Long {
        val articleLikeCount = articleLikeCountRepository.read(articleId)
        val articleCommentCount = articleCommentCountRepository.read(articleId)
        val articleViewCount = articleViewCountRepository.read(articleId)

        return articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT +
                articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT +
                articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT
    }

    companion object {
        private const val ARTICLE_LIKE_COUNT_WEIGHT = 3
        private const val ARTICLE_COMMENT_COUNT_WEIGHT = 2
        private const val ARTICLE_VIEW_COUNT_WEIGHT = 1
    }
}
