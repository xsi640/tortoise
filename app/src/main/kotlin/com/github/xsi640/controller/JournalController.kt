package com.github.xsi640.controller

import com.github.xsi640.entities.Journal
import com.github.xsi640.repository.JournalRepository
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


