package com.example.backend.dailyrecords

import com.example.backend.categories.Category
import com.example.backend.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
    name = "daily_records",
    indexes = [
        Index(name = "idx_daily_records_date", columnList = "date"),
        Index(name = "idx_daily_records_category", columnList = "category_id"),
    ],
)
class DailyRecord(
    @Column(nullable = false)
    var date: LocalDate,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
    @Column(nullable = true, length = 10)
    var memo: String? = null,
) : BaseEntity()
