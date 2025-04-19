package my.board.comment.controller

import my.board.comment.service.CommentService
import my.board.comment.service.CommentServiceV2
import my.board.comment.service.request.CommentCreateRequest
import my.board.comment.service.request.CommentCreateRequestV2
import my.board.comment.service.response.CommentPageResponse
import my.board.comment.service.response.CommentPageResponseV2
import my.board.comment.service.response.CommentResponse
import my.board.comment.service.response.CommentResponseV2
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentControllerV2(
    private val commentService: CommentServiceV2,
) {
    @GetMapping("/v2/comments/{commentId}")
    fun read(
        @PathVariable("commentId") commentId: Long,
    ): CommentResponseV2 = commentService.read(commentId)

    @PostMapping("/v2/comments")
    fun create(
        @RequestBody request: CommentCreateRequestV2,
    ): CommentResponseV2 = commentService.create(request)

    @DeleteMapping("/v2/comments/{commentId}")
    fun delete(
        @PathVariable("commentId") commentId: Long,
    ) {
        commentService.delete(commentId)
    }

    @GetMapping("/v2/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): CommentPageResponseV2 = commentService.readAll(articleId, page, pageSize)

    @GetMapping("/v2/comments/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastPath", required = false) lastPath: String?,
        @RequestParam("pageSize") pageSize: Long,
    ): List<CommentResponseV2> = commentService.readAllInfiniteScroll(articleId, lastPath, pageSize)

    @GetMapping("/v2/comments/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long,
    ): Long = commentService.count(articleId)

}
