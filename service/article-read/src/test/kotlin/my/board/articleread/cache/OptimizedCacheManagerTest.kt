package kuke.board.articleread.cache

import my.board.articleread.cache.OptimizedCache
import my.board.articleread.cache.OptimizedCacheLockProvider
import my.board.articleread.cache.OptimizedCacheManager
import my.board.articleread.cache.OptimizedCacheOriginDataSupplier
import my.board.common.dataserializer.DataSerializer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

@ExtendWith(MockitoExtension::class)
internal class OptimizedCacheManagerTest {
    @InjectMocks
    var optimizedCacheManager: OptimizedCacheManager? = null

    @Mock
    var stringRedisTemplate: StringRedisTemplate? = null

    @Mock
    var optimizedCacheLockProvider: OptimizedCacheLockProvider? = null

    @Mock
    lateinit var valueOperations: ValueOperations<String, String>

    @BeforeEach
    fun beforeEach() {
        valueOperations = Mockito.mock(ValueOperations::class.java) as ValueOperations<String, String>
        BDDMockito.given(stringRedisTemplate!!.opsForValue()).willReturn(valueOperations)
    }

    @Test
    @DisplayName("캐시 데이터가 없으면 원본 데이터 요청")
    @Throws(Throwable::class)
    fun processShouldCallOriginDataIfCachedDataIsNull() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val cachedData: String? = null
        BDDMockito.given(valueOperations["testType::1::param"]).willReturn(cachedData)

        // when
        val result = optimizedCacheManager!!.process(type, ttlSeconds, args, returnType, originDataSupplier)

        // then
        Assertions.assertThat(result).isEqualTo(originDataSupplier.get())
        Mockito.verify(valueOperations).set(
            ArgumentMatchers.eq("testType::1::param"), ArgumentMatchers.anyString(), ArgumentMatchers.any(
                Duration::class.java
            )
        )
    }

    @Test
    @DisplayName("유효하지 않은 캐시 데이터라면 원본 데이터 요청")
    @Throws(Throwable::class)
    fun processShouldCallOriginDataIfInvalidCachedData() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val cachedData = "{::invalid"
        BDDMockito.given(valueOperations["testType::1::param"]).willReturn(cachedData)

        // when
        val result = optimizedCacheManager!!.process(type, ttlSeconds, args, returnType, originDataSupplier)

        // then
        Assertions.assertThat(result).isEqualTo(originDataSupplier.get())
        Mockito.verify(valueOperations).set(
            ArgumentMatchers.eq("testType::1::param"), ArgumentMatchers.anyString(), ArgumentMatchers.any(
                Duration::class.java
            )
        )
    }

    @Test
    @DisplayName("논리적으로 만료되지 않은 데이터면 캐시 데이터 반환")
    @Throws(Throwable::class)
    fun processShouldReturnCachedDataIfNotExpiredLogically() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier= OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache = OptimizedCache.of("cached", Duration.ofSeconds(ttlSeconds))
        val cachedData: String = DataSerializer.serialize(optimizedCache)!!
        BDDMockito.given(valueOperations["testType::1::param"]).willReturn(cachedData)

        // when
        val result = optimizedCacheManager!!.process(type, ttlSeconds, args, returnType, originDataSupplier)

        // then
        Assertions.assertThat(result).isEqualTo("cached")
        Mockito.verify(valueOperations, Mockito.never()).set(
            ArgumentMatchers.eq("testType::1::param"), ArgumentMatchers.anyString(), ArgumentMatchers.any(
                Duration::class.java
            )
        )
    }

    @Test
    @DisplayName("논리적으로 만료된 데이터면 락 획득 시도. 락 실패 시 캐시 데이터 반환")
    @Throws(
        Throwable::class
    )
    fun processShouldReturnCachedDataIfExpiredLogicallyAndLockNotAcquired() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache = OptimizedCache.of("cached", Duration.ofSeconds(-1))
        val cachedData: String = DataSerializer.serialize(optimizedCache)!!
        BDDMockito.given(valueOperations["testType::1::param"]).willReturn(cachedData)

        BDDMockito.given(optimizedCacheLockProvider!!.lock("testType::1::param")).willReturn(false)

        // when
        val result = optimizedCacheManager!!.process(type, ttlSeconds, args, returnType, originDataSupplier)

        // then
        Assertions.assertThat(result).isEqualTo("cached")
        Mockito.verify(valueOperations, Mockito.never()).set(
            ArgumentMatchers.eq("testType::1::param"), ArgumentMatchers.anyString(), ArgumentMatchers.any(
                Duration::class.java
            )
        )
    }

    @Test
    @DisplayName("논리적으로 만료된 데이터면 락 획득 시도. 락 성공 시 캐시 리프레시 후 반환")
    @Throws(
        Throwable::class
    )
    fun processShouldCallOriginDataAndRefreshCacheIfExpiredLogicallyAndLockAcquired() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache = OptimizedCache.of("cached", Duration.ofSeconds(-1))
        val cachedData: String = DataSerializer.serialize(optimizedCache)!!
        BDDMockito.given(valueOperations["testType::1::param"]).willReturn(cachedData)

        BDDMockito.given(optimizedCacheLockProvider!!.lock("testType::1::param")).willReturn(true)

        // when
        val result = optimizedCacheManager!!.process(type, ttlSeconds, args, returnType, originDataSupplier)

        // then
        Assertions.assertThat(result).isEqualTo("origin")
        Mockito.verify(valueOperations).set(
            ArgumentMatchers.eq("testType::1::param"), ArgumentMatchers.anyString(), ArgumentMatchers.any(
                Duration::class.java
            )
        )
    }
}
