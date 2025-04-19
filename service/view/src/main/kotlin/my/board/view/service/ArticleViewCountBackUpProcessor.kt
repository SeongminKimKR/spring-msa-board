package my.board.view.service

import my.board.view.entitiy.ArticleViewCount
import my.board.view.repository.ArticleViewCountBackupRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val articleViewCountBackupRepository: ArticleViewCountBackupRepository,
) {

    @Transactional
    fun backUp(
        articleId: Long,
        viewCount: Long,
    ) {
        val result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount)

        if (result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                .ifPresentOrElse(
                    { /* 존재할 경우 아무것도 하지 않음 */ },
                    { articleViewCountBackupRepository.save(ArticleViewCount(articleId, viewCount)) }
                )
        }
    }
}
