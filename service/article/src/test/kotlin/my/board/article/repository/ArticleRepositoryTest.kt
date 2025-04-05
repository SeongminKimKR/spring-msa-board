package my.board.article.repository

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.context.SpringBootTest
import java.util.logging.Logger

@SpringBootTest
class ArticleRepositoryTest(
    private val articleRepository: ArticleRepository
) : FunSpec({

    val logger = Logger.getLogger(ArticleRepository::class.simpleName)

    test("findAll()") {
        val articles = articleRepository.findAll(1L, 14999970L, 30L)
        for (article in articles) {
            logger.info("article =$article")
        }
    }

    test("count()") {
        val count = articleRepository.count(1L, 10000L)
        logger.info("count =$count")
    }
})
