package com.album.figurinha.model

data class Figurinha(
    val id: Int,
    val name: String,
    val image: String,
    val rarity: Int,
    val playerId: Int? = null,
    val teamId: Int? = null,
    val coachId: Int? = null
)