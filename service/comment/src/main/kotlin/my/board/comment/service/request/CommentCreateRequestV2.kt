package my.board.comment.service.request

data class CommentCreateRequestV2(
    val articleId: Long,
    val content: String,
    val parentPath: String? = null,
    val writerId: Long,
)
