package my.board.article.data

import io.kotest.core.spec.style.FunSpec
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kotlinx.coroutines.*
import my.board.article.entity.Article
import my.board.common.snowflake.Snowflake
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.atomic.AtomicInteger

const val EXECUTE_COUNT = 2000
const val BULK_INSERT_SIZE = 6000

@SpringBootTest
class DataInitializer(
    @PersistenceContext
    val entityManager: EntityManager,
    val transactionTemplate: TransactionTemplate,
) : FunSpec({
//
//    val snowflake = Snowflake()
//    val globalIndex = AtomicInteger(0)
//
//    suspend fun insert() = withContext(Dispatchers.IO) {
//        transactionTemplate.executeWithoutResult {
//            repeat(BULK_INSERT_SIZE) {
//                val index = globalIndex.getAndIncrement()
//                val article = Article(
//                    snowflake.nextId(),
//                    1L,
//                    1L,
//                    "title$index",
//                    "content$index",
//                )
//                entityManager.persist(article)
//            }
//        }
//    }
//
//    test("initialize") {
//        runBlocking {
//            coroutineScope {
//                repeat(EXECUTE_COUNT) {
//                    launch {
//                        insert()
//                    }
//                }
//            }
//        }
//    }
})
