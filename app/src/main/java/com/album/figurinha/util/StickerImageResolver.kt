package com.album.figurinha.util

/**
 * Resolvedor de URLs de imagens para jogadores, escudos e bandeiras.
 *
 * Agora usa as URLs fornecidas diretamente pela API-Sports sempre que possivel.
 * O mapeamento SoFIFA permanece como fallback para jogadores/equipes conhecidos
 * quando a URL da API nao e suficiente.
 */
object StickerImageResolver {

    private val playerToSoFifaMap = mapOf(
        154 to "158023",  // Messi
        614 to "190871",  // Neymar
        732 to "238794",  // Vinicius Jr
        276 to "231747",  // Mbappe
        874 to "20801",   // Cristiano Ronaldo
        474 to "193080",  // E. Martinez
    )

    private val teamToSoFifaMap = mapOf(
        6 to "1370", // Brazil
        8 to "1369", // Argentina
        9 to "1362", // Spain
    )

    private val teamToIsoMap = mapOf(
        6 to "br",
        8 to "ar",
        9 to "es"
    )

    fun getPlayerImageUrl(playerId: Int, originalUrl: String): String {
        val url = if (originalUrl.startsWith("http://")) {
            originalUrl.replace("http://", "https://")
        } else {
            originalUrl
        }
        if (url.isNotEmpty() && !url.contains("sofifa")) {
            return url
        }
        val soFifaId = playerToSoFifaMap[playerId]
        return if (soFifaId != null) {
            val paddedId = soFifaId.padStart(6, '0')
            "https://cdn.sofifa.net/players/${paddedId.take(3)}/${paddedId.takeLast(3)}/24_120.png"
        } else {
            url
        }
    }

    fun getTeamShieldUrl(teamId: Int, originalUrl: String): String {
        val url = if (originalUrl.startsWith("http://")) {
            originalUrl.replace("http://", "https://")
        } else {
            originalUrl
        }
        if (url.isNotEmpty() && !url.contains("sofifa")) {
            return url
        }
        val soFifaTeamId = teamToSoFifaMap[teamId]
        return if (soFifaTeamId != null) {
            "https://cdn.sofifa.net/teams/$soFifaTeamId/120.png"
        } else {
            url
        }
    }

    fun getCountryFlagUrl(teamId: Int): String {
        val isoCode = teamToIsoMap[teamId] ?: "un"
        return "https://cdn.sofifa.net/flags/$isoCode@3x.png"
    }
}
