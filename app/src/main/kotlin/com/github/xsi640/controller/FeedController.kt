package com.github.xsi640.controller

import com.github.xsi640.mqtt.TortoiseMq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/mqtt")
@RestController
class FeedController {

    @Autowired
    lateinit var mq: TortoiseMq

    @PostMapping("feed")
    fun feed(@RequestBody request: FeedRequest) {
        mq.feed(request.count)
    }

    data class FeedRequest(
        val count: Int = 1
    )
}