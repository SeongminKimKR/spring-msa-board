package my.board.like.service

import my.board.common.snowflake.Snowflake
import my.board.like.entity.ArticleLike
import my.board.like.repository.ArticleLikeRepository
import my.board.like.service.response.ArticleLikeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService (
    private val articleLikeRepository: ArticleLikeRepository,
){
    fun read(
        articleId: Long,
        userId: Long,
    ): ArticleLikeResponse = articleLikeRepository.findByArticleIdAndUserId(articleId,userId)
        .map(ArticleLikeResponse::from)
        .orElseThrow()

    @Transactional
    fun like(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.save(
            ArticleLike.from(Snowflake.nextId(),articleId,userId)
        )
    }

    @Transactional
    fun unlike(
        articleId: Long,
        userId: Long,
    ) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent(articleLikeRepository::delete)
    }
}
