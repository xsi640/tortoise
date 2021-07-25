package com.github.xsi640.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.*

@RequestMapping("/api/v1/journal")
@RestController
class JournalController {

    @Autowired
    private lateinit var journalRepository: JournalRepository

    @PostMapping
    fun save(@RequestBody journal: Journal): Journal {
        return journalRepository.save(journal)
    }

    @GetMapping
    fun get(): List<Journal> {
        return journalRepository.findAll()
    }
}

@Entity
@Table(name = "journal")
class Journal(
    @Id
    @SequenceGenerator(name = "journal_id_seq", sequenceName = "journal_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_id_seq")
    @Column(name = "id")
    var id: Int = 0,
    @Column
    var message: String = "",
    @Column
    var type: JournalType = JournalType.FEED,
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date = Date()
)

interface JournalRepository : JpaRepository<Journal, Long>

enum class JournalType(val code: Int) {
    FEED(1), Filter(2)
}