package my.board.common.dataserializer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory


object DataSerializer {
    private val objectMapper: ObjectMapper = initialize()
    private val logger = LoggerFactory.getLogger(DataSerializer::class.java)

    private fun initialize(): ObjectMapper =
        jacksonObjectMapper().registerModules(JavaTimeModule())

    fun <T> deserialize(data: String?, clazz: Class<T>): T? {
        try {
            return objectMapper.readValue(data, clazz)
        } catch (e: JsonProcessingException) {
            logger.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, e)
            return null
        }
    }

    fun <T> deserialize(data: Any?, clazz: Class<T>): T {
        return objectMapper.convertValue(data, clazz)
    }

    fun serialize(`object`: Any?): String? {
        try {
            return objectMapper.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            logger.error("[DataSerializer.serialize] object={}", `object`, e)
            return null
        }
    }
}
