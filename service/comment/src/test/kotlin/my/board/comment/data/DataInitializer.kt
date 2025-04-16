package my.board.article.data

import io.kotest.core.spec.style.FunSpec
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import my.board.comment.CommentApplication
import my.board.comment.entity.Comment
import my.board.common.snowflake.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


const val EXECUTE_COUNT = 2000
const val BULK_INSERT_SIZE = 6000

@SpringBootTest(classes = [CommentApplication::class])
class DataInitializer(
    @PersistenceContext
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate,
) : FunSpec({
    fun insert() =
        transactionTemplate.executeWithoutResult {
            var prev: Comment? = null
            for (i in 0 until BULK_INSERT_SIZE) {
                val parentCommentId = if (i % 2 == 0) null else prev?.commentId
                val comment = Comment(
                    Snowflake.nextId(),
                    "content",
                    parentCommentId,
                    1L,
                    1L
                )
                prev = comment
                entityManager.persist(comment)
            }

        }


    test("initialize") {
        val latch = CountDownLatch(EXECUTE_COUNT)
        val executorService = Executors.newFixedThreadPool(10)
        for (i in 0..<EXECUTE_COUNT) {
            executorService.submit {
                insert()
                latch.countDown()
                println("latch.getCount() = " + latch.getCount())
            }
        }
        latch.await()
        executorService.shutdown()
    }
})
