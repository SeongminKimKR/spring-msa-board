package my.board.article.data

import io.kotest.core.spec.style.FunSpec
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kotlinx.coroutines.*
import my.board.comment.CommentApplication
import my.board.comment.entity.Comment
import my.board.common.snowflake.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate

const val EXECUTE_COUNT = 2000
const val BULK_INSERT_SIZE = 6000

@SpringBootTest(classes = [CommentApplication::class])
class DataInitializer(
    @PersistenceContext
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate,
) : FunSpec({
    suspend fun insert() = withContext(Dispatchers.IO) {
        transactionTemplate.executeWithoutResult {
            var prev: Comment? = null
            repeat(BULK_INSERT_SIZE) { i ->
                val id = Snowflake.nextId()
                val comment = Comment(
                    id,
                    "content",
                    if (i % 2 == 0) id else prev!!.commentId,
                    1L,
                    1L,
                )

                prev = comment
                entityManager.persist(comment)
            }
        }
    }

    test("initialize") {
        runBlocking {
            coroutineScope {
                repeat(EXECUTE_COUNT) {
                    launch {
                        insert()
                    }
                }
            }
        }
    }
})
