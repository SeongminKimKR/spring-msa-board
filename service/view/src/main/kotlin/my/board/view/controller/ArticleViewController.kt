package my.board.view.controller

import my.board.view.service.ArticleViewService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleViewController(
    private val articleViewService: ArticleViewService,
) {

    @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
    fun increase(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ): Long = articleViewService.increase(articleId, userId)

    @GetMapping("/v1/article-views/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long,
    ): Long = articleViewService.count(articleId)
}
