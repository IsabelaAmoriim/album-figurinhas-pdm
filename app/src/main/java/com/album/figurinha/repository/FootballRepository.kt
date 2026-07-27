package com.album.figurinha.repository
import com.album.figurinha.api.ApiClient

class FootballRepository {
    private val api = ApiClient.api

    suspend fun getTeams() =
        api.getTeams(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            league = 1,
            season = 2026
        )
    suspend fun getPlayers(teamId: Int) =
        api.getPlayers(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            team = teamId,
            season = 2026
        )
    suspend fun getCoach(teamId: Int) =
        api.getCoach(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            team = teamId
        )
}