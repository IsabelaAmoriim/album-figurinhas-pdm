package com.album.figurinha.model

data class Competition(
    val id: Int,
    val name: String,
    val edition: String,
    val trophyImage: String,
    val description: String,
    val teams: List<Team> = emptyList()
)