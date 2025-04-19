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

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun like(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.like(articleId, userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun unlike(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ) {
        articleLikeService.unlike(articleId, userId)
    }
}
