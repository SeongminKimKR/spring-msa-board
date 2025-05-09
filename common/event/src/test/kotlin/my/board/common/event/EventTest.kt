package my.board.common.event

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import my.board.common.event.payload.ArticleCreatedEventPayload
import java.time.LocalDateTime

class EventTest : FunSpec({

    test("serde") {
        val payload = ArticleCreatedEventPayload(
            articleId = 1L,
            title = "title",
            content = "content",
            boardId = 1L,
            writerId = 1L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardArticleCount = 23L
        )

        val event = Event.of(
            1234L,
            EventType.ARTICLE_CREATED,
            payload
        )

        val json = event.toJson()
        println("json = $json")

        val result = Event.fromJson(json)

        result!!.eventId shouldBe event.eventId
        result.type shouldBe event.type
        result.payload::class shouldBe  payload::class

        val resultPayload =  result.payload as ArticleCreatedEventPayload

        resultPayload.articleId shouldBe  payload.articleId
        resultPayload.title shouldBe  payload.title
        resultPayload.createdAt shouldBe  payload.createdAt
    }
})
