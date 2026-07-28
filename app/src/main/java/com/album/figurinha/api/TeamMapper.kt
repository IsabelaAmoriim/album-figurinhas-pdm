package com.album.figurinha.api

import com.album.figurinha.api.dto.TeamDto
import com.album.figurinha.api.dto.TeamItemDto
import com.album.figurinha.model.Team

object TeamMapper {

    fun fromDto(item: TeamItemDto): Team {
        val dto = item.team
        return Team(
            id = dto.id,
            name = dto.name,
            shield = dto.logo,
            primaryColor = "",
            secondaryColor = "",
            description = "",
            titles = 0,
            players = emptyList(),
            coach = null
        )
    }

    fun enrichTeam(item: TeamItemDto, titles: Int = 0, description: String = ""): Team {
        val t = fromDto(item)
        return t.copy(titles = titles, description = description)
    }
}
