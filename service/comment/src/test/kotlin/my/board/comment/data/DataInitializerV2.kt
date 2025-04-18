package my.board.article.data

import io.kotest.core.spec.style.FunSpec
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import my.board.comment.CommentApplication
import my.board.comment.entity.CommentPath
import my.board.comment.entity.CommentV2
import my.board.common.snowflake.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest(classes = [CommentApplication::class])
class DataInitializerV2(
    @PersistenceContext
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate,
) : FunSpec() {
    fun insert(
        start: Int,
        end: Int,
    ) =
        transactionTemplate.executeWithoutResult {
            for (i in start until end) {
                val comment = CommentV2(
                    Snowflake.nextId(),
                    "content",
                    toPath(i),
                    1L,
                    1L
                )
                entityManager.persist(comment)
            }

        }

    private fun toPath(value: Int): CommentPath {
        var v = value
        var path = ""
        for (i in 0 until DEPTH_CHUNK_SIZE) {
            path = CHARSET[v % CHARSET.length] + path
            v /= CHARSET.length
        }

        return CommentPath.from(path)
    }

    init {
        test("initialize") {
            val latch = CountDownLatch(EXECUTE_COUNT)
            val executorService = Executors.newFixedThreadPool(10)
            for (i in 0..<EXECUTE_COUNT) {
                val start = i * BULK_INSERT_SIZE
                val end = (i + 1) * BULK_INSERT_SIZE
                executorService.submit {
                    insert(start, end)
                    latch.countDown()
                    println("latch.getCount() = " + latch.getCount())
                }
            }
            latch.await()
            executorService.shutdown()
        }
    }

    companion object {
        private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstvwxyz"
        private const val DEPTH_CHUNK_SIZE = 5
    }
}
