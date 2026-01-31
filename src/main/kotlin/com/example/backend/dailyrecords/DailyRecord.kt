package com.example.backend.dailyrecords

import com.example.backend.categories.Category
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "daily_records",
    indexes = [
        Index(name = "idx_daily_records_date", columnList = "date"),
        Index(name = "idx_daily_records_category", columnList = "category_id"),
    ],
)
class DailyRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false)
    var date: LocalDate,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
    @Column(nullable = true, length = 10)
    var memo: String? = null,
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null,
)
