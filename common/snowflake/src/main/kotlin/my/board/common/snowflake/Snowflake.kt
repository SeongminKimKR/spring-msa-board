package my.board.common.snowflake

import java.util.random.RandomGenerator

class Snowflake {
    private val nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1)
    private var lastTimeMillis = startTimeMillis
    private var sequence = 0L

    @Synchronized
    fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()

        check(currentTimeMillis >= lastTimeMillis) { "Invalid Time" }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) and maxSequence
            if (sequence == 0L) {
                currentTimeMillis = waitNextMillis(currentTimeMillis)
            }
        } else {
            sequence = 0
        }

        lastTimeMillis = currentTimeMillis

        return (((currentTimeMillis - startTimeMillis) shl (NODE_ID_BITS + SEQUENCE_BITS))
                or (nodeId shl SEQUENCE_BITS)
                or sequence)
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var currentTimestamp = currentTimestamp
        while (currentTimestamp <= lastTimeMillis) {
            currentTimestamp = System.currentTimeMillis()
        }
        return currentTimestamp
    }

    companion object {
        const val UNUSED_BITS: Int = 1
        const val EPOCH_BITS: Int = 41
        const val NODE_ID_BITS: Int = 10
        const val SEQUENCE_BITS: Int = 12

        const val maxNodeId: Long = (1L shl NODE_ID_BITS) - 1
        const val maxSequence: Long = (1L shl SEQUENCE_BITS) - 1


        // UTC = 2024-01-01T00:00:00Z
        private const val startTimeMillis = 1704067200000L


    }
}
