package my.board.like.service

import my.board.common.snowflake.Snowflake
import my.board.like.entity.ArticleLike
import my.board.like.entity.ArticleLikeCount
import my.board.like.repository.ArticleLikeCountRepository
import my.board.like.repository.ArticleLikeRepository
import my.board.like.service.response.ArticleLikeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) {
    fun read(
        articleId: Long,
        userId: Long,
    ): ArticleLikeResponse = articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
        .map(ArticleLikeResponse::from)
        .orElseThrow()

    /**
     * update 구문
     */
    @Transactional
    fun likePessimisticLock1(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.save(
            ArticleLike.from(Snowflake.nextId(), articleId, userId)
        )

        val result = articleLikeCountRepository.increase(articleId)

        if (result == 0) {
            //최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화한다.
            // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
            articleLikeCountRepository.save(ArticleLikeCount(articleId, 1L))
        }
    }

    @Transactional
    fun unlikePessimisticLock1(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent { articleLike ->
                articleLikeRepository.delete(articleLike)
                articleLikeCountRepository.decrease(articleId)
            }
    }

    /**
     * select ... for update + update 구문
     */
    @Transactional
    fun likePessimisticLock2(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.save(
            ArticleLike.from(Snowflake.nextId(), articleId, userId)
        )

        articleLikeCountRepository.findLockedByArticleId(articleId)
            .orElseGet { ArticleLikeCount(articleId, 0L) }
            .apply { increase() }
            .let { articleLikeCountRepository.save(it) }
    }

    @Transactional
    fun unlikePessimisticLock2(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent { articleLike ->
                articleLikeRepository.delete(articleLike)
                articleLikeCountRepository.findLockedByArticleId(articleId)
                    .orElseThrow()
                    .decrease()
            }
    }

    @Transactional
    fun likeOptimisticLock(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.save(
            ArticleLike.from(Snowflake.nextId(), articleId, userId)
        )

        articleLikeCountRepository.findById(articleId)
            .orElseGet { ArticleLikeCount(articleId, 0L) }
            .apply { increase() }
            .let { articleLikeCountRepository.save(it) }
    }

    @Transactional
    fun unlikeOptimisticLock(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent { articleLike ->
                articleLikeRepository.delete(articleLike)
                articleLikeCountRepository.findById(articleId)
                    .orElseThrow()
                    .decrease()
            }
    }

    fun count(articleId: Long): Long {
        return articleLikeCountRepository.findById(articleId)
            .map(ArticleLikeCount::likeCount)
            .orElse(0L)
    }
}
