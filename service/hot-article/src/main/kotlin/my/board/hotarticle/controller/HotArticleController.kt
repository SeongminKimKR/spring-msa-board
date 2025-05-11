package my.board.hotarticle.controller

import my.board.hotarticle.service.HotArticleService
import my.board.hotarticle.service.response.HotArticleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HotArticleController(
    private val hotArticleService: HotArticleService,
) {
    @GetMapping("/v1/hot-articles/articles/date/{dateStr}")
    fun readAll(
        @PathVariable("dateStr") dateStr: String,
    ): List<HotArticleResponse> = hotArticleService.readAll(dateStr)
}
