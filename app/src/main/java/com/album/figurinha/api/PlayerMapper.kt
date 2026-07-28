package com.album.figurinha.api

import com.album.figurinha.api.dto.PlayerDto
import com.album.figurinha.api.dto.PlayerItemDto
import com.album.figurinha.api.dto.PlayerStatisticsDto
import com.album.figurinha.model.Player
import com.album.figurinha.model.PlayerDetails
import com.album.figurinha.model.StickerRarity

object PlayerMapper {

    private const val WORLD_CUP_LEAGUE_ID = 1

    fun fromDto(item: PlayerItemDto, teamId: Int): Player? {
        val dto = item.player
        // Filtra para usar apenas estatisticas da Copa do Mundo (leagueId=1)
        val stats = item.statistics.firstOrNull { it.league?.id == WORLD_CUP_LEAGUE_ID }
            ?: item.statistics.firstOrNull() ?: return null
        val games = stats.games
        val number = games?.number ?: 0
        val position = games?.position ?: "Unknown"

        return Player(
            id = dto.id,
            name = dto.name,
            photo = dto.photo,
            number = number,
            position = normalizePosition(position),
            description = buildDescription(dto, stats),
            teamId = teamId,
            rarity = computeRarity(stats)
        )
    }

    fun toDetails(item: PlayerItemDto, teamId: Int): PlayerDetails? {
        val dto = item.player
        val stats = item.statistics.firstOrNull { it.league?.id == WORLD_CUP_LEAGUE_ID }
            ?: item.statistics.firstOrNull() ?: return null

        return PlayerDetails(
            id = dto.id,
            name = dto.name,
            photo = dto.photo,
            number = stats.games?.number ?: 0,
            position = normalizePosition(stats.games?.position ?: "Unknown"),
            teamId = teamId,
            age = dto.age,
            nationality = dto.nationality,
            height = dto.height,
            appearances = stats.games?.appearences,
            goals = stats.goals?.total,
            assists = stats.goals?.assists,
            rating = stats.games?.rating,
            minutes = stats.games?.minutes,
            yellowCards = stats.cards?.yellow,
            redCards = stats.cards?.red,
            passesKey = stats.passes?.key,
            tacklesTotal = stats.tackles?.total,
            dribblesSuccess = stats.dribbles?.total,
            duelsTotal = stats.duels?.total,
            duelsWon = stats.duels?.won,
            penaltiesScored = stats.penalty?.scored
        )
    }

    private fun normalizePosition(pos: String): String = when {
        pos.equals("Goalkeeper", true) -> "GOLEIRO"
        pos.equals("Defender", true) -> "ZAGUEIRO"
        pos.contains("Midfield", true) -> "MEIO-CAMPISTA"
        pos.contains("Attacker", true) || pos.contains("Forward", true) -> "ATACANTE"
        else -> pos.uppercase()
    }

    private fun buildDescription(dto: PlayerDto, stats: PlayerStatisticsDto): String {
        val parts = mutableListOf<String>()
        val nationality = dto.nationality
        val goals = stats.goals?.total
        val assists = stats.goals?.assists
        val appearences = stats.games?.appearences

        if (nationality != null) parts.add(nationality)

        if (appearences != null && goals != null && assists != null) {
            parts.add("$appearences jogos | $goals gols | $assists assistências")
        } else if (appearences != null) {
            parts.add("$appearences jogos")
        }

        return parts.joinToString(" · ")
    }

    fun computeRarity(stats: PlayerStatisticsDto): StickerRarity {
        val games = stats.games
        val goals = stats.goals
        val rating = games?.rating

        val appearences = games?.appearences ?: 0
        val totalGoals = goals?.total ?: 0
        val totalAssists = goals?.assists ?: 0
        val ratingValue = rating?.toDoubleOrNull() ?: 0.0

        return when {
            totalGoals >= 7 || ratingValue >= 8.0 -> StickerRarity.MYTHIC
            totalGoals >= 4 || totalAssists >= 4 || ratingValue >= 7.5 -> StickerRarity.LEGENDARY
            appearences >= 5 && (totalGoals >= 1 || totalAssists >= 2) -> StickerRarity.SPECIAL
            else -> StickerRarity.COMMON
        }
    }
}
