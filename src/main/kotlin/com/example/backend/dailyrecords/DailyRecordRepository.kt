package com.example.backend.dailyrecords

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyRecordRepository : JpaRepository<DailyRecord, Long> {
    fun findAllByDateOrderByDateAscIdAsc(date: LocalDate): List<DailyRecord>

    fun findAllByDateBetweenOrderByDateAscIdAsc(
        from: LocalDate,
        to: LocalDate,
    ): List<DailyRecord>

    fun findAllByOrderByDateAscIdAsc(): List<DailyRecord>
}
