package my.board.articleread.controller

import my.board.articleread.service.ArticleReadService
import my.board.articleread.service.response.ArticleReadResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleReadController (
    private val articleReadService: ArticleReadService,
){
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable("articleId") articleId: Long): ArticleReadResponse = articleReadService.read(articleId)
}
