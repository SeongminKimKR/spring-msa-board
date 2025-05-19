package my.board.articleread.repository

import my.board.common.dataserializer.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleQueryModelRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        val jsonString = DataSerializer.serialize(articleQueryModel)

        jsonString?.let {
            redisTemplate.opsForValue()
                .set(generateKey(articleQueryModel), it, ttl)
        }
    }

    fun update(articleQueryModel: ArticleQueryModel) {
        val jsonString = DataSerializer.serialize(articleQueryModel)

        jsonString?.let {
            redisTemplate.opsForValue()
                .setIfPresent(generateKey(articleQueryModel), it)
        }
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        return redisTemplate.opsForValue().get(generateKey(articleId))
            ?.let { json -> DataSerializer.deserialize(json, ArticleQueryModel::class.java) }
    }
    private fun generateKey(articleQueryModel: ArticleQueryModel): String = generateKey(articleQueryModel.articleId)

    private fun generateKey(articleId: Long): String = KEY_FORMAT.format(articleId)

    companion object {
        // article-read::article::{articleId}
        private const val KEY_FORMAT = "article-read::article::%s"
    }
}
