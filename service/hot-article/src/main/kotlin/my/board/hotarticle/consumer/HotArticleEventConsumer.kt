package my.board.hotarticle.consumer

import my.board.common.event.Event
import my.board.common.event.EventType
import my.board.hotarticle.service.HotArticleService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer (
    private val hotArticleService: HotArticleService
){
    private val logger = LoggerFactory.getLogger(HotArticleEventConsumer::class.java)

    @KafkaListener(topics = [
        EventType.Topic.MY_BOARD_ARTICLE,
        EventType.Topic.MY_BOARD_COMMENT,
        EventType.Topic.MY_BOARD_LIKE,
        EventType.Topic.MY_BOARD_VIEW,
    ])
    fun listen(
        message:String,
        ack: Acknowledgment
    ) {
        logger.info("[HotArticleEventConsumer.listen] received message={}", message)
        val event = Event.fromJson(message)

        event?.let { hotArticleService.handleEvent(it) }

        ack.acknowledge()
    }
}
