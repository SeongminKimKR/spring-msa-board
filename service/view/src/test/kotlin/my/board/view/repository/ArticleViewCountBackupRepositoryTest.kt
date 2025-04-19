package my.board.view.repository

import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import my.board.view.entitiy.ArticleViewCount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
class ArticleViewCountBackupRepositoryTest {
    @Autowired
    private lateinit var articleViewCountBackupRepository: ArticleViewCountBackupRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    @Transactional
    fun updateViewCountTest() {
        articleViewCountBackupRepository.save(ArticleViewCount(1L))

        entityManager.flush()
        entityManager.clear()

        val result1 = articleViewCountBackupRepository.updateViewCount(1L, 100L)
        val result2 = articleViewCountBackupRepository.updateViewCount(1L, 300L)
        val result3 = articleViewCountBackupRepository.updateViewCount(1L, 200L)

        result1 shouldBe 1
        result2 shouldBe 1
        result3 shouldBe 0

        val articleViewCount = articleViewCountBackupRepository.findById(1L).get()
        articleViewCount.viewCount shouldBe 300L
    }
}
