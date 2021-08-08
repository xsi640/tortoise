package com.github.xsi640.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import javax.annotation.PostConstruct

interface TortoiseMq {
    fun feed(count: Int)
}

@Service
class TortoiseMqImpl : TortoiseMq {

    private lateinit var client: MqttClient
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var config: TortoiseConfig

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    override fun feed(count: Int) {
        val bytes = objectMapper.writeValueAsBytes(FeedTortoiseMessage(count))
        client.publish(config.topic, MqttMessage(bytes))
        log.info("publish mq: ${config.topic} content: $count")
    }

    @PostConstruct
    fun initiailize() {
        val persistence = MemoryPersistence()
        this.client = MqttClient(config.brokerUrl, config.clientId, persistence)
        val ops = MqttConnectOptions()
        ops.userName = config.username
        ops.password = config.password.toCharArray()
        ops.isCleanSession = true
        this.client.connect(ops)
        log.info("connected mqtt server. broker:${config.brokerUrl} clientId:${config.clientId}")
    }
}

enum class TortoiseType(val code: Int) {
    FEED(1);

    companion object {
        fun codeOf(code: Int): TortoiseType {
            for (value in values()) {
                if (value.code == code)
                    return value
            }
            throw IllegalArgumentException()
        }
    }
}

open class TortoiseMessage(
    var type: Int = 0
)

class FeedTortoiseMessage(
    var feed: Int = 0
) : TortoiseMessage(TortoiseType.FEED.code)