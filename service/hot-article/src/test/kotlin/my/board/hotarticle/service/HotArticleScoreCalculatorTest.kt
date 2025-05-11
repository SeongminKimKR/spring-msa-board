package my.board.hotarticle.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import my.board.hotarticle.repository.ArticleCommentCountRepository
import my.board.hotarticle.repository.ArticleLikeCountRepository
import my.board.hotarticle.repository.ArticleViewCountRepository
import java.util.random.RandomGenerator

class HotArticleScoreCalculatorTest : FunSpec({
    val articleLikeCountRepository = mockk<ArticleLikeCountRepository>()
    val articleCommentCountRepository = mockk<ArticleCommentCountRepository>()
    val articleViewCountRepository = mockk<ArticleViewCountRepository>()
    val hotArticleScoreCalculator = HotArticleScoreCalculator(
        articleLikeCountRepository,
        articleViewCountRepository,
        articleCommentCountRepository
    )

    test("calculate") {
        val articleId = 1L
        val likeCount = RandomGenerator.getDefault().nextLong(100)
        val commentCount = RandomGenerator.getDefault().nextLong(100)
        val viewCount = RandomGenerator.getDefault().nextLong(100)

        every { articleLikeCountRepository.read(articleId) } returns likeCount
        every { articleCommentCountRepository.read(articleId) } returns commentCount
        every { articleViewCountRepository.read(articleId) } returns viewCount

        val score = hotArticleScoreCalculator.calculate(articleId)

        score shouldBe 3 * likeCount + 2 * commentCount + 1 * viewCount
    }

})
