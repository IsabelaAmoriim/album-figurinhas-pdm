package com.album.figurinha.repository

import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.util.StickerImageResolver

object PlayersData {
    val allPlayers = listOf(
        Player(614, "Neymar Jr", "", 10, "ATACANTE", "Astral do Brasil", 1, StickerRarity.SPECIAL),
        Player(732, "Vinícius Jr", "", 7, "ATACANTE", "Velocidade pura", 1, StickerRarity.SPECIAL),
        Player(154, "Lionel Messi", "", 10, "ATACANTE", "Capitão da Seleção Argentina", 2, StickerRarity.LEGENDARY),
        Player(474, "E. Martínez", "", 23, "GOLEIRO", "Paredão da Argentina", 2, StickerRarity.COMMON),
        Player(276, "K. Mbappé", "", 10, "ATACANTE", "Estrela da França", 3, StickerRarity.MYTHIC),
        Player(874, "C. Ronaldo", "", 7, "ATACANTE", "Lenda de Portugal", 4, StickerRarity.LEGENDARY)
    ).map {
        it.copy(photo = StickerImageResolver.getPlayerImageUrl(it.id, it.photo))
    }

    fun getPlayersForTeam(teamId: Int): List<Player> {
        return allPlayers.filter { it.teamId == teamId }
    }

    fun getPlayerById(id: Int): Player? {
        return allPlayers.find { it.id == id }
    }

    fun getTotalPlayersCount(): Int {
        return allPlayers.size
    }

    fun getRarityForPlayer(id: Int): StickerRarity {
        return getPlayerById(id)?.rarity ?: StickerRarity.COMMON
    }
}
