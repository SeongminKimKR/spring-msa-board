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

        /***
         * 예시: shardsCount = 64, appIds = 3개 (0, 1, 2)
         * 0: 0 ~ 20
         * 1: 21 ~ 41
         * 2: 42 ~ 63
         */
        private fun assign(appId: String, appIds: List<String>, shardCount: Long): List<Long> {
            val appIndex = findAppIndex(appId, appIds)

            // 할당 할 샤드가 없음
            if(appIndex == -1) {
                return emptyList()
            }

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1

            return LongRange(start, end).toList()
        }

        /***
         * 실행된 app들은 정렬된 상태
         * 특정 appid가 몇 번째에 있는지 확인
         * 그 인덱스를 바탕으로 polling 범위를 만듬
         */
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
