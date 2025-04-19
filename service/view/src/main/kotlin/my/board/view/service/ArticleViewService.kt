package my.board.view.service

import my.board.view.repository.ArticleViewCountRepository
import my.board.view.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository,
) {

    fun increase(
        articleId: Long,
        userId: Long,
    ): Long {
        if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
            return articleViewCountRepository.read(articleId)
        }

        val count = articleViewCountRepository.increase(articleId)
        if (count % BACK_UP_BATCH_SIZE == 0L) {
            articleViewCountBackUpProcessor.backUp(articleId, count)
        }

        return count
    }

    fun count(articleId: Long): Long = articleViewCountRepository.read(articleId)

    companion object {
        private const val BACK_UP_BATCH_SIZE = 100
        private val TTL = Duration.ofMinutes(10)
    }
}
