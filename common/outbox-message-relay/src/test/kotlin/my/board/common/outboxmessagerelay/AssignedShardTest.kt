package my.board.common.outboxmessagerelay

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.util.stream.Stream

class AssignedShardTest : FunSpec({
    test("of") {
        val shardCount = 64L
        val appList = listOf<String>("appId1", "appId2", "appId3")

        val assignedShard1 = AssignedShard.of(appList[0], appList, shardCount)
        val assignedShard2 = AssignedShard.of(appList[1], appList, shardCount)
        val assignedShard3 = AssignedShard.of(appList[2], appList, shardCount)
        val assignedShard4 = AssignedShard.of("invalid", appList, shardCount)


        // then
        val result: List<Long> = listOf(
            assignedShard1.shards,
            assignedShard2.shards,
            assignedShard3.shards,
            assignedShard4.shards
        ).flatten()

        result.size shouldBe shardCount.toInt()

        for (i in 0 until 64) {
            result[i] shouldBe i
        }

        assignedShard4.shards.isEmpty() shouldBe true
    }
})
