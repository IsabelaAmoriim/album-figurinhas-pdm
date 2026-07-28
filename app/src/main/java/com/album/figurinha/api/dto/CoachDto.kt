package com.album.figurinha.api.dto

data class CoachResponseDto(
    val response: List<CoachItemDto>
)

data class CoachItemDto(
    val id: Int,
    val name: String,
    val firstname: String?,
    val lastname: String?,
    val age: Int?,
    val birth: CoachBirthDto?,
    val nationality: String?,
    val photo: String?,
    val team: CoachTeamDto?
)

data class CoachBirthDto(
    val date: String?,
    val place: String?,
    val country: String?
)

data class CoachTeamDto(
    val id: Int,
    val name: String,
    val logo: String?
)
