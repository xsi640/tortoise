package com.github.xsi640.mqtt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("tortoise.mqtt")
class TortoiseConfig(
    var brokerUrl: String = "",
    var clientId: String = "",
    var topic: String = "",
    var username: String = "",
    var password: String = ""
)