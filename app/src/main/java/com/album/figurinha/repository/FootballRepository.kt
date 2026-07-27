package com.album.figurinha.repository
import com.album.figurinha.api.ApiClient

open class FootballRepository {
    private val api by lazy { ApiClient.api }

    open suspend fun getTeams() =
        api.getTeams(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            league = 1,
            season = 2026
        )
    open suspend fun getPlayers(teamId: Int) =
        api.getPlayers(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            team = teamId,
            season = 2026
        )
    open suspend fun getCoach(teamId: Int) =
        api.getCoach(
            apiKey = "9946a0ee6f4786aa0f4efdee47ed5448",
            team = teamId
        )
}