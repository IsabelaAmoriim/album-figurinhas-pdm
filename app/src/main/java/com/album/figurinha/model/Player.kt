package com.album.figurinha.model

data class Player(
    val id: Int,
    val name: String,
    val photo: String,
    val number: Int,
    val position: String,
    val description: String,
    val teamId: Int,
    val rarity: StickerRarity = StickerRarity.COMMON
)
data class PlayerStatistics(
    val team: PlayerTeam
)
data class PlayerTeam(
    val id: Int,
    val name: String,
    val logo: String
)
data class PlayerItem(
    val player: Player,
    val statistics: List<PlayerStatistics>
)
data class PlayerResponse(
    val response: List<PlayerItem>
)