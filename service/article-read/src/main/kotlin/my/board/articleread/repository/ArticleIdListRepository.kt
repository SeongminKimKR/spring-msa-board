package my.board.articleread.repository

import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit.limit
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleIdListRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun add(
        boardId: Long,
        articleId: Long,
        limit: Long,
    ) {
        redisTemplate.executePipelined(RedisCallback<Any> { action ->
            val conn = action as StringRedisConnection
            val key = generateKey(boardId)

            conn.zAdd(key, 0.0, toPaddedString(articleId))
            conn.zRemRange(key, 0, -limit - 1)
            null
        } as RedisCallback<*>)
    }

    fun delete(
        boardId: Long,
        articleId: Long,
    ) {
        redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId))
    }

    fun readAll(
        boardId: Long,
        offset: Long,
        limit: Long,
    ): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRange(generateKey(boardId), offset, offset + limit - 1)
            ?.map(String::toLong)
            ?: emptyList()
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        lastArticleId: Long?,
        limit: Long,
    ): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeByLex(
                generateKey(boardId),
                // 6 5 4 3 2 1
                // lastArticleId가 null이면 최초 limit(3)만큼 조회 후
                // lastArticleId = 4
                // 4제외 후 조회 3 2 1
                lastArticleId?.let { Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))) }
                    ?: Range.unbounded(),
                limit().count(limit.toInt())
            )
            ?.map(String::toLong)
            ?: emptyList()
    }

    private fun toPaddedString(articleId: Long): String {
        // 1234 -> 0000000000000001234
        return "%019d".format(articleId)
    }

    private fun generateKey(boardId: Long) = KEY_FORMAT.format(boardId)

    companion object {
        //article-read::board::{boardId}::article-list
        private const val KEY_FORMAT = "article-read::board::%s::article-list"
    }
}
