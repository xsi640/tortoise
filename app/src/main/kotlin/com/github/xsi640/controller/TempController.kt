package com.github.xsi640.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.persistence.*


@RequestMapping("/api/v1/feed")
@RestController
class TempController {

    @Autowired
    private lateinit var tempLogRepository: TempLogRepository

    @PostMapping
    fun save() {
        tempLogRepository.save(TempLog())
    }
}

@Entity
@Table(name = "temp_log")
class TempLog(
    @Id
    @SequenceGenerator(name = "record_id_seq", sequenceName = "record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "record_id_seq")
    @Column(name = "id")
    var id: Int = 0,
    @Column
    var temp: Float = 0.0F,
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date = Date()
)

interface TempLogRepository : JpaRepository<TempLog, Long>