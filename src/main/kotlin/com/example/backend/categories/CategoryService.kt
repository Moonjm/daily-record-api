package com.example.backend.categories

import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val repository: CategoryRepository,
) {
    fun list(active: Boolean?): List<CategoryResponse> {
        val entities =
            if (active == null) {
                repository.findAllByOrderBySortOrderAscIdAsc()
            } else {
                repository.findAllByIsActiveOrderBySortOrderAscIdAsc(active)
            }
        return entities.map { it.toResponse() }
    }

    fun get(id: Long): CategoryResponse =
        repository.findByIdOrNull(id)?.toResponse()
            ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)

    @Transactional
    fun create(request: CategoryRequest): CategoryResponse {
        val emoji = request.emoji.trim()
        val name = request.name.trim()
        validateRequired(emoji, name)

        if (repository.existsByEmoji(emoji)) {
            throw CustomException(ErrorCode.DUPLICATE_RESOURCE, emoji)
        }

        val nextSortOrder = (repository.findTopByOrderBySortOrderDescIdDesc()?.sortOrder ?: 0) + 1
        val entity =
            Category(
                emoji = emoji,
                name = name,
                isActive = request.isActive,
                sortOrder = nextSortOrder,
            )
        return repository.save(entity).toResponse()
    }

    @Transactional
    fun update(
        id: Long,
        request: CategoryRequest,
    ): CategoryResponse {
        val entity =
            repository.findByIdOrNull(id)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)

        val emoji = request.emoji.trim()
        val name = request.name.trim()
        validateRequired(emoji, name)

        if (emoji != entity.emoji && repository.existsByEmoji(emoji)) {
            throw CustomException(ErrorCode.DUPLICATE_RESOURCE, emoji)
        }

        entity.updateDetails(emoji = emoji, name = name, isActive = request.isActive)

        return repository.save(entity).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val entity =
            repository.findByIdOrNull(id)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)
        repository.delete(entity)
    }

    @Transactional
    fun move(request: CategoryMoveRequest) {
        val beforeId = request.beforeId
        if (beforeId == request.targetId) {
            throw CustomException(ErrorCode.INVALID_REQUEST, "targetId")
        }

        val entities = repository.findAllByOrderBySortOrderAscIdAsc().toMutableList()
        val targetIndex = entities.indexOfFirst { it.id == request.targetId }
        if (targetIndex < 0) {
            throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, request.targetId)
        }
        val target = entities.removeAt(targetIndex)

        val insertIndex =
            beforeId
                ?.let { id ->
                    val idx = entities.indexOfFirst { it.id == id }
                    if (idx < 0) {
                        throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, id)
                    }
                    idx
                }
                ?: entities.size

        entities.add(insertIndex, target)

        entities.forEachIndexed { index, entity ->
            entity.updateSortOrder(index + 1)
        }
        repository.saveAll(entities)
    }

    private fun validateRequired(
        emoji: String,
        name: String,
    ) {
        if (emoji.isBlank()) {
            throw CustomException(ErrorCode.INVALID_REQUEST, "emoji")
        }
        if (name.isBlank()) {
            throw CustomException(ErrorCode.INVALID_REQUEST, "name")
        }
    }
}
