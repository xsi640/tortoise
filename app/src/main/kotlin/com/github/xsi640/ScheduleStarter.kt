package com.github.xsi640

import com.github.xsi640.entities.SettingsKeys
import com.github.xsi640.entities.SettingsRepository
import com.github.xsi640.mqtt.TortoiseMq
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledFuture

@Component
class ScheduleStarter : CommandLineRunner {

    private val log = LoggerFactory.getLogger(ScheduleStarter::class.java)

    @Autowired
    private lateinit var taskScheduler: TaskScheduler

    @Autowired
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var feedTask: ScheduledFuture<*>

    @Autowired
    lateinit var mq: TortoiseMq

    @Scheduled(cron = "0 0 8 * * ?")
    fun feed() {
        mq.feed(2)
    }

    override fun run(vararg args: String?) {
        reloadFeedTask()
    }

    fun reloadFeedTask() {
        val feedCountSettings = settingsRepository.findByKey(SettingsKeys.FEED_COUNT.name)
            ?: throw IllegalArgumentException("The settings ${SettingsKeys.FEED_COUNT.name} not found.")
        val feedCronSettings = settingsRepository.findByKey(SettingsKeys.FEED_COUNT.name)
            ?: throw IllegalArgumentException("The settings ${SettingsKeys.FEED_CRON.name} not found.")
        val feedCount = feedCountSettings.value.toInt()
        val cron = feedCronSettings.value
        feedTask = taskScheduler.schedule({
            mq.feed(feedCount)
        }, CronTrigger(cron))!!
        log.info("add feed schedule. cron:$cron count:$feedCount")
    }

    fun reloadSettings(key: String) {
        when (key) {
            SettingsKeys.FEED_CRON.name, SettingsKeys.FEED_COUNT.name -> reloadFeedTask()
        }
    }
}