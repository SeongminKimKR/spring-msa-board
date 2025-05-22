package my.board.articleread.cache

import java.lang.annotation.ElementType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OptimizedCacheable(
    val type: String,
    val ttlSeconds: Long,
)
