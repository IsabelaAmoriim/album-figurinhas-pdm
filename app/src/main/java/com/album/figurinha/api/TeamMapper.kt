package com.album.figurinha.api

import com.album.figurinha.api.dto.TeamItemDto
import com.album.figurinha.model.Team

object TeamMapper {

    private val nameTranslations = mapOf(
        "Brazil" to "Brasil",
        "Argentina" to "Argentina",
        "Spain" to "Espanha",
        "Portugal" to "Portugal",
        "France" to "França",
        "Germany" to "Alemanha",
        "Italy" to "Itália",
        "Netherlands" to "Países Baixos",
        "England" to "Inglaterra",
        "Belgium" to "Bélgica",
        "Croatia" to "Croácia",
        "Switzerland" to "Suíça",
        "Uruguay" to "Uruguai",
        "Japan" to "Japão",
        "South Korea" to "Coreia do Sul",
        "Senegal" to "Senegal",
        "Morocco" to "Marrocos",
        "Cameroon" to "Camarões",
        "Ghana" to "Gana",
        "Tunisia" to "Tunísia",
        "Iran" to "Irã",
        "Saudi Arabia" to "Arábia Saudita",
        "Australia" to "Austrália",
        "Canada" to "Canadá",
        "Mexico" to "México",
        "United States" to "Estados Unidos",
        "Costa Rica" to "Costa Rica",
        "Ecuador" to "Equador",
        "Serbia" to "Sérvia",
        "Denmark" to "Dinamarca",
        "Poland" to "Polônia",
        "Wales" to "País de Gales",
        "Qatar" to "Catar",
    )

    fun fromDto(item: TeamItemDto): Team {
        val dto = item.team
        return Team(
            id = dto.id,
            name = nameTranslations[dto.name] ?: dto.name,
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
