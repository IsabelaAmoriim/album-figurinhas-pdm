package com.album.figurinha.repository

import com.album.figurinha.api.ApiClient
import com.album.figurinha.api.CoachMapper
import com.album.figurinha.api.PlayerMapper
import com.album.figurinha.api.TeamMapper
import com.album.figurinha.model.Coach
import com.album.figurinha.model.Player
import com.album.figurinha.model.PlayerDetails
import com.album.figurinha.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

open class FootballRepository {
    private val api by lazy { ApiClient.api }
    private val apiKey = "9293fec2dcfdc7a871c3bb4389dede0b"
    private val worldCupLeagueId = 1
    private val worldCupSeason = 2022
    private val selectedTeams = setOf(6, 9, 8) // Brasil (6), Argentina (9), Espanha (8)

    private var teamsCache: List<Team>? = null
    private var playersCache: MutableMap<Int, List<Player>> = mutableMapOf()
    private var coachesCache: MutableMap<Int, Coach> = mutableMapOf()
    private var playerDetailsCache: MutableMap<Int, PlayerDetails> = mutableMapOf()

    open suspend fun getTeams(): List<Team> {
        teamsCache?.let { return it }

        android.util.Log.d("FootballRepo", "getTeams: chamando API league=$worldCupLeagueId season=$worldCupSeason")
        val response = withContext(Dispatchers.IO) {
            api.getTeams(
                apiKey = apiKey,
                league = worldCupLeagueId,
                season = worldCupSeason
            )
        }
        android.util.Log.d("FootballRepo", "getTeams: results=${response.results}, response.size=${response.response.size}")
        val teams = response.response.map { TeamMapper.fromDto(it) }
        android.util.Log.d("FootballRepo", "getTeams: ${teams.size} times mapeados. Primeiro: ${teams.firstOrNull()?.name}")
        teamsCache = teams
        return teams
    }

    open suspend fun getTeamById(teamId: Int): Team? {
        return getTeams().find { it.id == teamId }
    }

    open suspend fun getPlayers(teamId: Int): List<Player> {
        if (playersCache.containsKey(teamId)) {
            return playersCache[teamId] ?: emptyList()
        }

        // Usa apenas pagina 1 para economizar chamadas de API (plano free: 100/dia).
        // Cada pagina retorna ate 20 jogadores, suficiente para o album.
        val response = withContext(Dispatchers.IO) {
            api.getPlayersByTeamAndPage(
                apiKey = apiKey,
                team = teamId,
                season = worldCupSeason,
                page = 1
            )
        }

        val players = response.response.mapNotNull { item ->
            PlayerMapper.fromDto(item, teamId)
        }

        playersCache[teamId] = players
        return players
    }

    open suspend fun getPlayerDetails(teamId: Int, playerId: Int): PlayerDetails? {
        if (playerDetailsCache.containsKey(playerId)) {
            return playerDetailsCache[playerId]
        }

        val response = withContext(Dispatchers.IO) {
            api.getPlayersByTeamAndPage(
                apiKey = apiKey,
                team = teamId,
                season = worldCupSeason,
                page = 1
            )
        }

        val match = response.response.firstOrNull { it.player.id == playerId }
        if (match != null) {
            val details = PlayerMapper.toDetails(match, teamId)
            if (details != null) {
                playerDetailsCache[playerId] = details
            }
            return details
        }

        return null
    }

    open suspend fun getCoach(teamId: Int): Coach? {
        if (coachesCache.containsKey(teamId)) {
            return coachesCache[teamId]
        }

        return try {
            val response = withContext(Dispatchers.IO) {
                api.getCoach(apiKey = apiKey, team = teamId)
            }
            val coach = response.response.firstOrNull()?.let {
                CoachMapper.fromDto(it, teamId)
            }
            if (coach != null) {
                coachesCache[teamId] = coach
            }
            coach
        } catch (e: Exception) {
            null
        }
    }

    /** Carrega apenas os times selecionados (Brasil, Argentina, Espanha) */
    open suspend fun loadAllWorldCupData(): LoadResult {
        val teams = getTeams().filter { it.id in selectedTeams }
        android.util.Log.d("FootballRepo", "loadAllWorldCupData: ${teams.size} times selecionados")

        val resultTeams = mutableListOf<Team>()
        var totalPlayers = 0
        var requestCount = 1 // já contando o getTeams()

        for ((index, team) in teams.withIndex()) {
            // Delay entre requisicoes para evitar rate limit (429)
            if (index > 0) {
                delay(1500) // 1.5s entre cada time
            }

            try {
                requestCount++
                val players = getPlayers(team.id)

                requestCount++
                val coach = getCoach(team.id)

                resultTeams.add(team.copy(players = players, coach = coach))
                totalPlayers += players.size
                android.util.Log.d("FootballRepo", "${team.name}: ${players.size} jogadores (req #$requestCount)")
            } catch (e: Exception) {
                android.util.Log.e("FootballRepo", "Falha ao carregar ${team.name}: ${e.message}")
                resultTeams.add(team)
            }
        }

        android.util.Log.d("FootballRepo", "Total: $totalPlayers jogadores em ${resultTeams.size} times, $requestCount requisicoes")
        return LoadResult(
            teams = resultTeams,
            playerCount = totalPlayers
        )
    }

    data class LoadResult(
        val teams: List<Team>,
        val playerCount: Int
    )
}
