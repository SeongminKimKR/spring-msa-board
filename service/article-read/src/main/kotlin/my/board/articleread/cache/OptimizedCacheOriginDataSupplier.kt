package my.board.articleread.cache

fun interface OptimizedCacheOriginDataSupplier<T> {
    fun get(): T
}
