package my.board.comment.entity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CommentPathTest : FunSpec() {

    private fun createChildCommentTest(
        commentPath: CommentPath,
        descendantsTopPath: String?,
        expectedChildPath: String,
    ) {
        val childCommentPath = commentPath.createChildCommentPath(descendantsTopPath)
        childCommentPath.path shouldBe expectedChildPath
    }
    init {
        test("createChildCommentTest") {
            // 000000 <- 생성
            createChildCommentTest(CommentPath.from(""), null, "00000")

            // 000000
            //          000000 <- 생성
            createChildCommentTest(CommentPath.from("00000"), null, "0000000000")

            // 000000
            // 000001 <- 생성
            createChildCommentTest(CommentPath.from(""), "00000", "00001")

            // 00000z
            //      abcdz
            //          zzzzz
            //              zzzzz
            //      abce0 <- 생성
            createChildCommentTest(CommentPath.from("0000z"), "0000zabcdzzzzzzzzzz", "0000zabce0")
        }

        test("createChildrenPathIfMaxDepthTest") {
            shouldThrow<IllegalStateException> {  CommentPath.from("zzzzz".repeat(5)).createChildCommentPath(null)}
        }

        test("createChildCommentPathIfChunkOverflowTest") {
            val commentPath = CommentPath.from("")
            shouldThrow<IllegalStateException> {
                commentPath.createChildCommentPath("zzzzz")
            }
        }
    }
}
