package my.board.articleread.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardArticleCountRepository (
    private val redisTemplate: StringRedisTemplate,
){
    fun createOrUpdate(
        boardId: Long,
        articleCount: Long,
    ) {
        redisTemplate.opsForValue().set(generateKey(boardId), articleCount.toString())
    }

    fun read(boardId: Long): Long {
        return redisTemplate.opsForValue().get(generateKey(boardId))?.toLong()
            ?: 0L
    }
    private fun generateKey(boardId: Long): String = KEY_FORMAT.format(boardId)

    companion object {
        // article-read::board-article-count::board::{boardId}
        private const val KEY_FORMAT = "article-read::board-article-count::board::%s"
    }
}
