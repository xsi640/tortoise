package com.github.xsi640.controller

import com.github.xsi640.ScheduleStarter
import com.github.xsi640.entities.Settings
import com.github.xsi640.entities.SettingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotBlank

@RequestMapping("/api/v1/settings")
@RestController
class SettingsController {
    @Autowired
    lateinit var settingsRepository: SettingsRepository

    @Autowired
    lateinit var scheduleStarter: ScheduleStarter

    @GetMapping("{key}")
    fun getValue(@PathVariable("key") key: String): String {
        val settings = settingsRepository.findByKey(key)
        return settings?.value ?: ""
    }

    @Transactional
    @PostMapping
    fun setValue(@RequestBody request: SettingsRequest): Settings {
        val settings = settingsRepository.findByKey(request.key)
        return if (settings == null) {
            val new = settingsRepository.save(
                Settings(
                    key = request.key,
                    value = request.value
                )
            )
            scheduleStarter.reloadSettings(request.key)
            new
        } else {
            if (settings.value != request.value) {
                settings.value = request.value
                settingsRepository.save(settings)
            }
            scheduleStarter.reloadSettings(request.key)
            settings
        }
    }

    data class SettingsRequest(
        @field:NotBlank
        val key: String,
        @field:NotBlank
        val value: String
    )
}