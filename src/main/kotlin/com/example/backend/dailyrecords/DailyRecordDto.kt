package com.example.backend.dailyrecords

import com.example.backend.categories.CategoryResponse
import com.example.backend.categories.toResponse
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "일상기록 응답")
data class DailyRecordResponse(
    @field:Schema(description = "ID", example = "1")
    val id: Long,
    @field:Schema(description = "날짜", example = "2026-02-01")
    val date: LocalDate,
    @field:Schema(description = "메모", example = "퇴근후")
    val memo: String?,
    @field:Schema(description = "카테고리")
    val category: CategoryResponse,
)

fun DailyRecord.toResponse(): DailyRecordResponse =
    DailyRecordResponse(
        id = id ?: 0L,
        date = date,
        memo = memo,
        category = category.toResponse(),
    )

@Schema(description = "일상기록 요청")
data class DailyRecordRequest(
    @field:Schema(description = "날짜", example = "2026-02-01")
    val date: LocalDate,
    @field:Schema(description = "카테고리 ID", example = "3")
    val categoryId: Long,
    @field:Schema(description = "메모", example = "퇴근후")
    @field:Size(max = 10)
    val memo: String? = null,
)
