package my.board.comment.entity

import jakarta.persistence.Embeddable

@Embeddable
class CommentPath(
    val path: String,
) {

    fun getDepth() = calDepth(path)

    fun isRoot() = calDepth(path) == 1

    fun getParentPath() = path.substring(0, path.length - DEPTH_CHUNK_SIZE)

    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if (descendantsTopPath == null) {
            return from(path + MIN_CHUNK)
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return from(increase(childrenTopPath))
    }

    private fun findChildrenTopPath(descendantsTopPath: String): String =
        descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE)

    private fun increase(childrenTopPath: String): String {
        // 00000 00000
        val lastChunk = childrenTopPath.substring(childrenTopPath.length - DEPTH_CHUNK_SIZE)

        check(!isChunkOverflowed(lastChunk)) { "chunk overflowed" }

        val charsetLength = CHARSET.length
        var value = 0

        for (ch in lastChunk.toCharArray()) {
             value = value * charsetLength + CHARSET.indexOf(ch)
        }

        value += 1

        var result = ""

        for(i in 0 until DEPTH_CHUNK_SIZE) {
            result = CHARSET[value % charsetLength] + result
            value /= charsetLength
        }

        return childrenTopPath.substring(0, childrenTopPath.length - DEPTH_CHUNK_SIZE) + result
    }

    private fun isChunkOverflowed(lastChunk: String) = MAX_CHUNK.equals(lastChunk)

    companion object {
        private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstvwxyz"

        private const val DEPTH_CHUNK_SIZE = 5
        private const val MAX_DEPTH = 5

        private val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        private val MAX_CHUNK = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun from(path: String): CommentPath {
            check(!isDepthOverflowed(path)) { "depth overflowed" }

            return CommentPath(path)
        }

        private fun isDepthOverflowed(path: String) = calDepth(path) > MAX_DEPTH

        private fun calDepth(path: String): Int = path.length / DEPTH_CHUNK_SIZE
    }
}
