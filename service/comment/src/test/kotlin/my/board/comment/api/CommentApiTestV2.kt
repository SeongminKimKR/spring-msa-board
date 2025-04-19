package my.board.comment.api

import io.kotest.core.spec.style.FunSpec
import my.board.comment.service.request.CommentCreateRequestV2
import my.board.comment.service.response.CommentPageResponseV2
import my.board.comment.service.response.CommentResponse
import my.board.comment.service.response.CommentResponseV2
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class CommentApiTestV2 : FunSpec() {
    val restClient = RestClient.create("http://localhost:9001")

    val create: (CommentCreateRequestV2) -> CommentResponseV2? = { request ->
        restClient.post()
            .uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponseV2::class.java)
    }

    val read: (Long) -> CommentResponseV2? = { commentId ->
        restClient.get()
            .uri("/v2/comments/{commentId}", commentId)
            .retrieve()
            .body(CommentResponseV2::class.java)
    }

    init {
        test("create") {
            val response1 = create(CommentCreateRequestV2(1L, "my comment1", null, 1L))
            val response2 = create(CommentCreateRequestV2(1L, "my comment2", response1!!.path, 1L))
            val response3 = create(CommentCreateRequestV2(1L, "my comment3", response2!!.path, 1L))

            println("commentId=${response1.commentId}")
            println("\tcommentId=${response2.commentId}")
            println("\tcommentId=${response3!!.commentId}")

        }

        test("read") {
            println("response = ${read(171601617595994112)}")
        }

        test("delete") {
            /**
             * commentId=171605739792416768
             * 	commentId=171605740689997824
             * 	commentId=171605740840992768
             */
            restClient.delete()
                .uri("/v2/comments/{commentId}", 171605740840992768)
                .retrieve()
                .toBodilessEntity()
        }

        test("readAll") {
            val response = restClient.get()
                .uri("/v2/comments?articleId=1&page=50000&pageSize=10")
                .retrieve()
                .body(CommentPageResponseV2::class.java)!!

            println("response.commentCount = ${response.commentCount}")
            for (comment in response.comments) {
                println("comment.commentId = ${comment.commentId}")
            }

            /**
             * response.commentCount = 101
             * comment.commentId = 171608056051400709
             * comment.commentId = 171608056089149445
             * comment.commentId = 171608056089149450
             * comment.commentId = 171608056093343747
             * comment.commentId = 171608056093343758
             * comment.commentId = 171608056093343761
             * comment.commentId = 171608056093343764
             * comment.commentId = 171608056093343767
             * comment.commentId = 171608056093343769
             * comment.commentId = 171608056093343794
             */
        }

        test("readAllInfiniteScroll") {
            val response1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CommentResponseV2>>() {})!!

            println("first page")

            for (comment in response1) {
                println("comment.commentId = ${comment.commentId}")
            }

            val lastPath = response1.last().path
            val response2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".format(lastPath))
                .retrieve()
                .body(object : ParameterizedTypeReference<List<CommentResponseV2>>() {})!!

            println("second page")

            for (comment in response2) {
                println("comment.commentId = ${comment.commentId}")
            }
        }

        test("count") {
            val response = create(CommentCreateRequestV2(2L, "my comment1", null, 1L))

            val count1 = restClient.get()
                .uri("/v2/comments/articles/${response!!.articleId}/count")
                .retrieve()
                .body(Long::class.java)

            println("count1 = $count1")

            restClient.delete()
                .uri("/v2/comments/{commentId}", response.commentId)
                .retrieve()
                .toBodilessEntity()

            val count2 = restClient.get()
                .uri("/v2/comments/articles/${response!!.articleId}/count")
                .retrieve()
                .body(Long::class.java)

            println("count2 = $count2")
        }
    }
}

