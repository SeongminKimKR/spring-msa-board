package my.board.comment.api

import io.kotest.core.spec.style.FunSpec
import my.board.comment.service.request.CommentCreateRequest
import my.board.comment.service.response.CommentPageResponse
import my.board.comment.service.response.CommentResponse
import org.springframework.core.ParameterizedTypeReference
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

        test("readAll") {
            val response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse::class.java)!!

            println("response.commentCount = ${response.commentCount}")
            for (comment in response.comments) {
                if(comment.commentId != comment.parentCommentId) {
                    print("\t")
                }
                println("comment.commentId = ${comment.commentId}")
            }

            /***
             * comment.commentId = 170932160339619840
             * 	comment.commentId = 170932160385757189
             * comment.commentId = 170932160339619841
             * 	comment.commentId = 170932160385757193
             * comment.commentId = 170932160339619842
             * 	comment.commentId = 170932160385757192
             * comment.commentId = 170932160339619843
             * 	comment.commentId = 170932160385757185
             * comment.commentId = 170932160339619844
             * 	comment.commentId = 170932160385757188
             */
        }

        test("readAllInfiniteScroll") {
            val response1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})!!

            println("first page")

            for (comment in response1) {
                if(comment.commentId != comment.parentCommentId) {
                    print("\t")
                }
                println("comment.commentId = ${comment.commentId}")
            }

            val lastParentCommentId = response1.last().parentCommentId
            val lastCommentId = response1.last().commentId
            val response2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s".format(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})!!

            println("second page")

            for (comment in response2) {
                if(comment.commentId != comment.parentCommentId) {
                    print("\t")
                }
                println("comment.commentId = ${comment.commentId}")
            }
        }
    }
}

