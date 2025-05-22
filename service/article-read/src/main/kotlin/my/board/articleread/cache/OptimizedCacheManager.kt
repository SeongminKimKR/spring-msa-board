package my.board.articleread.cache

import my.board.common.dataserializer.DataSerializer
import my.board.common.dataserializer.DataSerializer.deserialize
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component


@Component
class OptimizedCacheManager(
    private val redisTemplate: StringRedisTemplate,
    private val optimizedCacheLockProvider: OptimizedCacheLockProvider,
) {
    fun process(
        type: String,
        ttlSeconds: Long,
        args: Array<Any>,
        returnType: Class<*>,
        originDataSupplier: OptimizedCacheOriginDataSupplier<*>,
    ): Any {
        val key = generateKey(type, args)

        val cachedData = redisTemplate.opsForValue().get(key) ?: return refresh(originDataSupplier, key, ttlSeconds)

        val optimizedCache = deserialize(cachedData, OptimizedCache::class.java)
            ?: return refresh(originDataSupplier, key, ttlSeconds)

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType)
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType)
        }

        return try {
            refresh(originDataSupplier, key, ttlSeconds)
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }
    }

    private fun refresh(
        originDataSupplier: OptimizedCacheOriginDataSupplier<*>,
        key: String,
        ttlSeconds: Long,
    ): Any {
        val result = originDataSupplier.get() ?: throw IllegalStateException("fail to supply")

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)

        result?.let {
            val optimizedCache = OptimizedCache.of(it, optimizedCacheTTL.logicalTTL)
            val data = DataSerializer.serialize(optimizedCache) ?: throw IllegalStateException("fail to serialize")
            redisTemplate.opsForValue()
                .set(
                    key,
                    data,
                    optimizedCacheTTL.physicalTTL
                )
        }

        return result
    }

    private fun generateKey(prefix: String, args: Array<Any>): String {
        // prefix = a, args = [1,2]
        // a::1::2
        return prefix + DELIMITER + args.joinToString(DELIMITER)
    }

    companion object {
        private const val DELIMITER = "::"
    }
}
