package com.github.xsi640.repository

import com.github.xsi640.entities.Journal
import org.springframework.data.jpa.repository.JpaRepository

interface JournalRepository : JpaRepository<Journal, Long>