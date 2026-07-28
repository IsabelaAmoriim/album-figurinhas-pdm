package com.album.figurinha.api
import com.album.figurinha.model.TeamResponse
import com.album.figurinha.model.CoachResponse
import com.album.figurinha.model.PlayerResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface FootballApi {

    @GET("teams")
    suspend fun getTeams(
        @Header("x-apisports-key")
        apiKey: String,

        @Query("league")
        league: Int,

        @Query("season")
        season: Int
    ): TeamResponse



    @GET("players")
    suspend fun getPlayers(
        @Header("x-apisports-key")
        apiKey: String,

        @Query("team")
        team: Int,

        @Query("season")
        season: Int
    ): PlayerResponse



    @GET("coachs")
    suspend fun getCoach(
        @Header("x-apisports-key")
        apiKey: String,

        @Query("team")
        team: Int
    ): CoachResponse

}