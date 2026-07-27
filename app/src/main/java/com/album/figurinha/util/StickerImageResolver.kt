package com.album.figurinha.util

import android.util.Log

object StickerImageResolver {
    
    // Mapping API-Sports IDs to SoFIFA IDs (EA Sports FC 24)
    private val playerToSoFifaMap = mapOf(
        154 to "158023",  // Messi
        614 to "190871",  // Neymar
        732 to "238794",  // Vinicius Jr
        276 to "231747",  // Mbappe
        874 to "20801",   // Cristiano Ronaldo
        474 to "193080",  // E. Martinez
    )

    // Mapping Team IDs to SoFifa Team IDs (National Teams)
    private val teamToSoFifaMap = mapOf(
        1 to "1370", // Brazil (Official CBF Shield)
        2 to "1369", // Argentina (Official AFA Shield)
        3 to "1335", // France (Official FFF Shield)
        4 to "1354", // Portugal (Official FPF Shield)
    )

    // Mapping Team IDs to ISO Country Codes for Flags
    private val teamToIsoMap = mapOf(
        1 to "br",
        2 to "ar",
        3 to "fr",
        4 to "pt"
    )

    fun getPlayerImageUrl(playerId: Int, originalUrl: String): String {
        val soFifaId = playerToSoFifaMap[playerId]
        return if (soFifaId != null) {
            val paddedId = soFifaId.padStart(6, '0')
            // High quality headshot (120x120 is the standard high-res version on SoFifa)
            val url = "https://cdn.sofifa.net/players/${paddedId.take(3)}/${paddedId.takeLast(3)}/24_120.png"
            Log.d("ImageResolver", "Resolving Player $playerId to SoFifa: $url")
            url
        } else {
            originalUrl
        }
    }

    fun getTeamShieldUrl(teamId: Int, originalUrl: String): String {
        val soFifaTeamId = teamToSoFifaMap[teamId]
        return if (soFifaTeamId != null) {
            // High-resolution 120px Shield
            val url = "https://cdn.sofifa.net/teams/$soFifaTeamId/120.png"
            Log.d("ImageResolver", "Resolving Team $teamId to High-Res Shield: $url")
            url
        } else {
            originalUrl
        }
    }

    fun getCountryFlagUrl(teamId: Int): String {
        val isoCode = teamToIsoMap[teamId] ?: "un"
        // High-resolution Flag (@3x)
        return "https://cdn.sofifa.net/flags/$isoCode@3x.png"
    }
}