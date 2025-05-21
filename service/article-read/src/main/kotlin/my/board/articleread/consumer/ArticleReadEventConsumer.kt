package my.board.articleread.consumer

import my.board.articleread.service.ArticleReadService
import my.board.common.event.Event
import my.board.common.event.EventType
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ArticleReadEventConsumer (
    private val articleReadService: ArticleReadService,
){
    private val logger = LoggerFactory.getLogger(ArticleReadEventConsumer::class.java)
    @KafkaListener(topics = [
        EventType.Topic.MY_BOARD_ARTICLE,
        EventType.Topic.MY_BOARD_COMMENT,
        EventType.Topic.MY_BOARD_LIKE,
    ])
    fun listen(
        message: String,
        ack: Acknowledgment,
    ){
        logger.info("[ArticleReadEventConsumer.listen] message={}", message)
        val event = Event.fromJson(message)
        event?.let {
            articleReadService.handleEvent(it)
        }
        ack.acknowledge()
    }
}
