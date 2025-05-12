package my.board.common.outboxmessagerelay

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@EnableAsync
@Configuration
@ComponentScan("my.board.common.outboxmessagerelay")
@EnableScheduling
class MessageRelayConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configProps = mutableMapOf<String, Any>().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            this[ProducerConfig.ACKS_CONFIG] = "all"
        }

        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }

    @Bean
    fun messageRelayPublishEventExecutor(): Executor = ThreadPoolTaskExecutor()
        .apply {
            corePoolSize = 20
            maxPoolSize = 50
            queueCapacity = 100
            setThreadNamePrefix("mr-pub-event-")
        }

    @Bean
    fun messageRelayPublishPendingEventExecutor(): Executor = Executors.newSingleThreadScheduledExecutor()
}
