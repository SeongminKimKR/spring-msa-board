package my.board.articleread.cache

import com.fasterxml.jackson.annotation.JsonIgnore
import my.board.common.dataserializer.DataSerializer
import java.time.Duration
import java.time.LocalDateTime

data class OptimizedCache(
    val data: String?,
    val expiredAt: LocalDateTime
) {

    @JsonIgnore
    fun isExpired() = LocalDateTime.now().isAfter(expiredAt)

    fun <T> parseData(dataType: Class<T>): T {
        return DataSerializer.deserialize(data, dataType) ?: throw IllegalStateException("fail to deserialize")
    }

    companion object {
        fun of(
            data: Any,
            ttl: Duration
        ) = OptimizedCache(
            data = DataSerializer.serialize(data),
            expiredAt = LocalDateTime.now().plus(ttl)
        )
    }
}
