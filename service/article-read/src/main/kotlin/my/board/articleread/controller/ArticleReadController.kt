package my.board.articleread.controller

import my.board.articleread.service.ArticleReadService
import my.board.articleread.service.response.ArticleReadPageResponse
import my.board.articleread.service.response.ArticleReadResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleReadController(
    private val articleReadService: ArticleReadService,
) {
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable("articleId") articleId: Long): ArticleReadResponse = articleReadService.read(articleId)

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): ArticleReadPageResponse = articleReadService.readAll(boardId, page, pageSize)

    @GetMapping("/v1/articles/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("lastArticleId") lastArticleId: Long?,
        @RequestParam("pageSize") pageSize: Long,
    ): List<ArticleReadResponse> = articleReadService.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
}
