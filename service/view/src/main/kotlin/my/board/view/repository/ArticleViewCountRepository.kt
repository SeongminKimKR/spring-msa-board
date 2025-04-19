package my.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountRepository (
    private val redisTemplate: StringRedisTemplate,
){
    fun read(articleId: Long): Long {
        val result = redisTemplate.opsForValue().get(generateKey(articleId))
        return result?.toLong() ?: 0L
    }

    fun increase(articleId: Long): Long = redisTemplate.opsForValue()
        .increment(generateKey(articleId)) ?: throw IllegalStateException("redis error")

    private fun generateKey(articleId:Long): String = KEY_FORMAT.format(articleId)

    companion object {
        //view::article::{article_id}::view_count
        private const val KEY_FORMAT = "/view::article::%s::view_count"
    }
}
