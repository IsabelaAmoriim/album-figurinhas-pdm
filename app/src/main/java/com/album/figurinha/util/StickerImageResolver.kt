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
        1 to "1370", // Brazil
        2 to "1369", // Argentina
        3 to "1335", // France
        4 to "1354", // Portugal
    )

    private val teamToIsoMap = mapOf(
        1 to "br",
        2 to "ar",
        3 to "fr",
        4 to "pt"
    )

    fun getPlayerImageUrl(playerId: Int, originalUrl: String): String {
        if (originalUrl.isNotEmpty() && !originalUrl.contains("sofifa")) {
            return originalUrl
        }
        val soFifaId = playerToSoFifaMap[playerId]
        return if (soFifaId != null) {
            val paddedId = soFifaId.padStart(6, '0')
            "https://cdn.sofifa.net/players/${paddedId.take(3)}/${paddedId.takeLast(3)}/24_120.png"
        } else {
            originalUrl
        }
    }

    fun getTeamShieldUrl(teamId: Int, originalUrl: String): String {
        if (originalUrl.isNotEmpty() && !originalUrl.contains("sofifa")) {
            return originalUrl
        }
        val soFifaTeamId = teamToSoFifaMap[teamId]
        return if (soFifaTeamId != null) {
            "https://cdn.sofifa.net/teams/$soFifaTeamId/120.png"
        } else {
            originalUrl
        }
    }

    fun getCountryFlagUrl(teamId: Int): String {
        val isoCode = teamToIsoMap[teamId] ?: "un"
        return "https://cdn.sofifa.net/flags/$isoCode@3x.png"
    }
}
