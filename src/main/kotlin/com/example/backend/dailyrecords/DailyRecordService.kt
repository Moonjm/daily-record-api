package com.example.backend.dailyrecords

import com.example.backend.categories.CategoryRepository
import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class DailyRecordService(
    private val repository: DailyRecordRepository,
    private val categoryRepository: CategoryRepository,
) {
    @Transactional(readOnly = true)
    fun list(
        date: LocalDate?,
        from: LocalDate?,
        to: LocalDate?,
    ): List<DailyRecordResponse> {
        val entries =
            when {
                date != null -> {
                    repository.findAllByDateOrderByDateAscIdAsc(date)
                }

                from != null || to != null -> {
                    if (from == null || to == null) {
                        throw CustomException(ErrorCode.INVALID_REQUEST, "dateRange")
                    }
                    if (from.isAfter(to)) {
                        throw CustomException(ErrorCode.INVALID_REQUEST, "dateRange")
                    }
                    repository.findAllByDateBetweenOrderByDateAscIdAsc(from, to)
                }

                else -> {
                    repository.findAllByOrderByDateAscIdAsc()
                }
            }
        return entries.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): DailyRecordResponse =
        repository.findByIdOrNull(id)?.toResponse()
            ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)

    @Transactional
    fun create(request: DailyRecordRequest): DailyRecordResponse {
        val category =
            categoryRepository.findByIdOrNull(request.categoryId)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, request.categoryId)

        val entity =
            DailyRecord(
                date = request.date,
                category = category,
                memo = request.memo,
            )
        return repository.save(entity).toResponse()
    }

    @Transactional
    fun update(
        id: Long,
        request: DailyRecordRequest,
    ): DailyRecordResponse {
        val entity =
            repository.findByIdOrNull(id)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)

        val category =
            categoryRepository.findByIdOrNull(request.categoryId)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, request.categoryId)

        entity.date = request.date
        entity.category = category
        entity.memo = request.memo

        return repository.save(entity).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val entity =
            repository.findByIdOrNull(id)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)
        repository.delete(entity)
    }
}
