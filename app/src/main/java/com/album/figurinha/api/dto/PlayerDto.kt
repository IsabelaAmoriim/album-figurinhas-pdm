package com.album.figurinha.api.dto

import com.google.gson.annotations.SerializedName

data class PlayerResponseDto(
    val response: List<PlayerItemDto>,
    val paging: PagingDto? = null
)

data class PagingDto(
    val current: Int = 1,
    val total: Int = 1
)

data class PlayerItemDto(
    val player: PlayerDto,
    val statistics: List<PlayerStatisticsDto>
)

data class PlayerDto(
    val id: Int,
    val name: String,
    val firstname: String,
    val lastname: String,
    val age: Int?,
    val birth: BirthDto?,
    val nationality: String?,
    val height: String?,
    val weight: String?,
    val injured: Boolean?,
    val photo: String
)

data class BirthDto(
    val date: String?,
    val place: String?,
    val country: String?
)

data class PlayerStatisticsDto(
    val team: PlayerTeamDto?,
    val league: PlayerLeagueDto?,
    val games: PlayerGamesDto?,
    val substitutes: PlayerSubstitutesDto?,
    val shots: PlayerShotsDto?,
    val goals: PlayerGoalsDto?,
    val passes: PlayerPassesDto?,
    val tackles: PlayerTacklesDto?,
    val duels: PlayerDuelsDto?,
    val dribbles: PlayerDribblesDto?,
    val fouls: PlayerFoulsDto?,
    val cards: PlayerCardsDto?,
    val penalty: PlayerPenaltyDto?
)

data class PlayerTeamDto(
    val id: Int,
    val name: String,
    val logo: String
)

data class PlayerLeagueDto(
    val id: Int,
    val name: String,
    val country: String?,
    val logo: String?,
    val flag: String?,
    val season: Int?
)

data class PlayerGamesDto(
    val appearences: Int?,
    val lineups: Int?,
    val minutes: Int?,
    val number: Int?,
    val position: String?,
    val rating: String?,
    val captain: Boolean?
)

data class PlayerSubstitutesDto(
    val `in`: Int?,
    val out: Int?,
    val bench: Int?
)

data class PlayerShotsDto(
    val total: Int?,
    val on: Int?
)

data class PlayerGoalsDto(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
)

data class PlayerPassesDto(
    val total: Int?,
    val key: Int?,
    val accuracy: Int?
)

data class PlayerTacklesDto(
    val total: Int?,
    val blocks: Int?,
    val interceptions: Int?
)

data class PlayerDuelsDto(
    val total: Int?,
    val won: Int?
)

data class PlayerDribblesDto(
    val attempts: Int?,
    val success: Int?,
    val total: Int?,
    val past: Int?
)

data class PlayerFoulsDto(
    val drawn: Int?,
    val committed: Int?
)

data class PlayerCardsDto(
    val yellow: Int?,
    val yellowred: Int?,
    val red: Int?
)

data class PlayerPenaltyDto(
    val won: Int?,
    val scored: Int?,
    val missed: Int?,
    val saved: Int?
)
