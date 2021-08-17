package com.github.xsi640.controller

import com.github.xsi640.entities.*
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/journal")
@RestController
class JournalController {

    @Autowired
    private lateinit var journalRepository: JournalRepository

    @Autowired
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @PostMapping
    fun save(@RequestBody journal: Journal): Journal {
        return journalRepository.save(journal)
    }

    @GetMapping
    fun page(
        @RequestParam(name = "type", required = false) type: Int,
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Long,
        @RequestParam("size", required = false, defaultValue = "20") size: Long
    ): Paged<Journal> {
        val total = jpaQueryFactory.from(QJournal.journal).where(QJournal.journal.type.eq(JournalType.codeOf(type)))
            .fetchCount()
        val data = if (total > 0) {
            jpaQueryFactory.from(QJournal.journal).where(QJournal.journal.type.eq(JournalType.codeOf(type)))
                .orderBy(QJournal.journal.id.desc())
                .offset(page - 1).limit(size).fetch() as List<Journal>
        } else {
            emptyList()
        }
        return Paged(
            total,
            page,
            size,
            data
        )
    }
}


