package com.album.figurinha.api

import com.album.figurinha.api.dto.CoachItemDto
import com.album.figurinha.model.Coach

object CoachMapper {

    fun fromDto(dto: CoachItemDto, teamId: Int): Coach {
        return Coach(
            id = dto.id,
            name = dto.name,
            photo = dto.photo ?: "",
            role = "TREINADOR",
            description = buildDescription(dto),
            teamId = teamId
        )
    }

    private fun buildDescription(dto: CoachItemDto): String {
        val parts = mutableListOf<String>()
        if (dto.nationality != null) parts.add(dto.nationality)
        if (dto.age != null) parts.add("${dto.age} anos")
        return parts.joinToString(" · ")
    }
}
