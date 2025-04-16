package my.board.comment.api

import io.kotest.core.spec.style.FunSpec
import my.board.comment.service.request.CommentCreateRequest
import my.board.comment.service.response.CommentResponse
import org.springframework.web.client.RestClient

class CommentApiTest : FunSpec() {
    val restClient = RestClient.create("http://localhost:9001")

    val create: (CommentCreateRequest) -> CommentResponse? = { request ->
        restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    val read: (Long) -> CommentResponse? = { commentId ->
        restClient.get()
            .uri("/v1/comments/{commentId}", commentId)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    init {
        test("create") {
            val response1 = create(CommentCreateRequest(1L, "my comment1", null, 1L))
            val response2 = create(CommentCreateRequest(1L, "my comment2", response1!!.commentId, 1L))
            val response3 = create(CommentCreateRequest(1L, "my comment3", response1.commentId, 1L))

            println("commentId=${response1.commentId}")
            println("\tcommentId=${response2!!.commentId}")
            println("\tcommentId=${response3!!.commentId}")

        }

        test("read") {
            println("response = ${read(170913714214752256)}")
        }

        test("delete") {
//            commentId=170913714214752256
//            commentId=170913714927783936
//            commentId=170913715057807360
            restClient.delete()
                .uri("/v1/comments/{commentId}", 170913715057807360)
                .retrieve()
                .toBodilessEntity()
        }
    }
}

