package my.board.articleread.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect(
    private val optimizedCacheManager: OptimizedCacheManager,
) {
    @Around("@annotation(OptimizedCacheable)")
    fun around(jointPoint: ProceedingJoinPoint): Any {
        val cacheable = findAnnotation(jointPoint)

        return optimizedCacheManager.process(
            cacheable.type,
            cacheable.ttlSeconds,
            jointPoint.args,
            findReturnType(jointPoint),
            { jointPoint.proceed() }
        )
    }

    private fun findAnnotation(jointPoint: ProceedingJoinPoint): OptimizedCacheable {
        val signature = jointPoint.signature
        val methodSignature = signature as MethodSignature

        return methodSignature.method.getAnnotation(OptimizedCacheable::class.java)
    }

    private fun findReturnType(jointPoint: ProceedingJoinPoint): Class<*> {
        val signature = jointPoint.signature
        val methodSignature = signature as MethodSignature
        return methodSignature.returnType
    }
}
