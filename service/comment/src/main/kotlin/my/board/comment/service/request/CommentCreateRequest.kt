package my.board.comment.service.request

data class CommentCreateRequest(
    val articleId: Long,
    val content: String,
    val parentCommentId: Long? = null,
    val writerId: Long,
)
