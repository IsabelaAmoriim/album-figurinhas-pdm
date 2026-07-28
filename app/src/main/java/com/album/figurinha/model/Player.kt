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

/**
 * Detalhes completos do jogador vindos da API, usados na tela PlayerDetailScreen.
 * Mantém compatibilidade com o modelo anterior mas agora com dados reais.
 */
data class PlayerDetails(
    val id: Int,
    val name: String,
    val photo: String,
    val number: Int,
    val position: String,
    val teamId: Int,
    val age: Int? = null,
    val nationality: String? = null,
    val height: String? = null,
    val appearances: Int? = null,
    val goals: Int? = null,
    val assists: Int? = null,
    val rating: String? = null,
    val minutes: Int? = null,
    val yellowCards: Int? = null,
    val redCards: Int? = null,
    val passesKey: Int? = null,
    val tacklesTotal: Int? = null,
    val dribblesSuccess: Int? = null,
    val duelsTotal: Int? = null,
    val duelsWon: Int? = null,
    val penaltiesScored: Int? = null
) {
    fun toPlayer(): Player = Player(
        id = id,
        name = name,
        photo = photo,
        number = number,
        position = position,
        description = "$nationality · $appearances jogos | $goals gols",
        teamId = teamId
    )
}

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