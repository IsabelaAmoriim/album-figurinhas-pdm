package com.album.figurinha.api.dto

data class TeamResponseDto(
    val response: List<TeamItemDto>,
    val results: Int = 0
)

data class TeamItemDto(
    val team: TeamDto,
    val venue: VenueDto?
)

data class TeamDto(
    val id: Int,
    val name: String,
    val code: String?,
    val country: String?,
    val founded: Int?,
    val national: Boolean?,
    val logo: String
)

data class VenueDto(
    val id: Int?,
    val name: String?,
    val address: String?,
    val city: String?,
    val capacity: Int?,
    val surface: String?,
    val image: String?
)
