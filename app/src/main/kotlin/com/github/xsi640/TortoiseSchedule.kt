package com.github.xsi640

import com.github.xsi640.mqtt.TortoiseMq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TortoiseSchedule {

    @Autowired
    lateinit var mq: TortoiseMq

    @Scheduled(cron = "0 0 8 * * ?")
    fun feed() {
        mq.feed(2)
    }
}