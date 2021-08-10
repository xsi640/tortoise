package com.github.xsi640.entities

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
@Table(name = "settings", indexes = [Index(name = "idx_settings_key", columnList = "key", unique = true)])
class Settings(
    @Id
    @SequenceGenerator(name = "journal_id_seq", sequenceName = "journal_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_id_seq")
    @Column(name = "id")
    var id: Long = 0,
    @Column
    var key: String = "",
    @Column
    var value: String = ""
)

interface SettingsRepository : JpaRepository<Settings, Long> {
    fun findByKey(key: String): Settings?
}

enum class SettingsKeys {
    FEED_COUNT, FEED_CRON
}