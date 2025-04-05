package my.board.common.snowflake

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class SnowflakeTest : FunSpec({
    val threadCount = 10
    val repeatCount = 1000
    val idCount = 1000

    fun generateIdList(snowflake: Snowflake, count: Int): List<Long> {
        return List(count) { snowflake.nextId() }
    }

    test("Snowflake ID는 순차적으로 증가해야 하고, 중복이 없어야 한다") {
        val executor = Executors.newFixedThreadPool(threadCount)
        val futures = (1..repeatCount).map {
            executor.submit(Callable {
                generateIdList(Snowflake, idCount)
            })
        }

        val result = mutableListOf<Long>()

        futures.forEach { future ->
            val ids = future.get()
            // 내부 ID 리스트는 순차 증가해야 함
            for (i in 1 until ids.size) {
                ids[i] shouldBeGreaterThan  ids[i - 1]
            }
            result.addAll(ids)
        }

        // 전체 ID는 중복이 없어야 함
        result.distinct().size shouldBe repeatCount * idCount

        executor.shutdown()
    }

    test("Snowflake ID 성능 테스트 - 1000회 반복, 병렬 생성 시간 측정") {
        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(repeatCount)

        val start = System.nanoTime()
        repeat(repeatCount) {
            executor.submit {
                generateIdList(Snowflake, idCount)
                latch.countDown()
            }
        }

        latch.await()
        val end = System.nanoTime()
        println("⏱ Total time: ${(end - start) / 1_000_000} ms")

        executor.shutdown()
    }
})
