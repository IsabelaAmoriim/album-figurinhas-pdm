package com.album.figurinha.model

data class Coach(
    val id: Int,
    val name: String,
    val photo: String,
    val role: String,
    val description: String,
    val teamId: Int
)

data class CoachTeam(
    val id: Int,
    val name: String,
    val logo: String

)
data class CoachResponse(
    val response: List<Coach>
)