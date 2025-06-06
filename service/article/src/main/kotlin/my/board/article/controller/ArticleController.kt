package my.board.article.controller

import my.board.article.service.ArticleService
import my.board.article.service.request.ArticleCreateRequest
import my.board.article.service.request.ArticleUpdateRequest
import my.board.article.service.response.ArticlePageResponse
import my.board.article.service.response.ArticleResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val articleService: ArticleService
) {
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable articleId: Long): ArticleResponse = articleService.read(articleId)

    @GetMapping("/v1/articles")
    fun readALl(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): ArticlePageResponse = articleService.readAll(boardId, page, pageSize)

    @GetMapping("/v1/articles/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam(value = "lastArticleId", required = false) lastArticleId: Long?,
    ): List<ArticleResponse> = articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId)


    @PostMapping("/v1/articles")
    fun create(@RequestBody request: ArticleCreateRequest): ArticleResponse = articleService.create(request)

    @PutMapping("/v1/articles/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequest
    ): ArticleResponse = articleService.update(articleId, request)

    @DeleteMapping("/v1/articles/{articleId}")
    fun delete(
        @PathVariable articleId: Long,
    ): Unit = articleService.delete(articleId)

    @GetMapping("/v1/articles/boards/{boardId}/count")
    fun count(@PathVariable boardId: Long): Long = articleService.count(boardId)
}
