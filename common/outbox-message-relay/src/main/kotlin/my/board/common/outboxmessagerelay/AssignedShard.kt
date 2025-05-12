package my.board.common.outboxmessagerelay

import java.util.stream.LongStream

class AssignedShard(
    val shards: List<Long>,
) {
    companion object {
        fun of(
            appId: String,
            appIds: List<String>,
            shardCount: Long,
        ) = AssignedShard(
            shards = assign(appId, appIds, shardCount)
        )

        private fun assign(appId: String, appIds: List<String>, shardCount: Long): List<Long> {
            val appIndex = findAppIndex(appId, appIds)

            if(appIndex == -1) {
                return emptyList()
            }

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1

            return LongRange(start, end).toList()
        }

        private fun findAppIndex(appId: String, appIds: List<String>): Int {
            for (i in appIds.indices) {
                if (appIds[i] == appId) {
                    return i
                }
            }

            return -1
        }
    }
}
