package my.board.like.controller

import my.board.like.service.ArticleLikeService
import my.board.like.service.response.ArticleLikeResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleLikeController(
    private val articleLikeService: ArticleLikeService,
) {

    @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ): ArticleLikeResponse = articleLikeService.read(articleId, userId)

    @GetMapping("/v1/article-likes/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long,
    ): Long = articleLikeService.count(articleId)

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun likePessimisticLock1(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.likePessimisticLock1(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun unlikePessimisticLock1(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.unlikePessimisticLock1(articleId, userId)
    }

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun likePessimisticLock2(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.likePessimisticLock2(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun unlikePessimisticLock2(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.unlikePessimisticLock2(articleId, userId)
    }

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun likeOptimisticLock(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.likeOptimisticLock(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun unlikeOptimisticLock(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.unlikeOptimisticLock(articleId, userId)
    }
}
