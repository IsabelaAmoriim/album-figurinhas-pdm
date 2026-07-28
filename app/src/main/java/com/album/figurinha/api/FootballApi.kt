package com.album.figurinha.api

import com.album.figurinha.api.dto.CoachResponseDto
import com.album.figurinha.api.dto.PlayerResponseDto
import com.album.figurinha.api.dto.TeamResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FootballApi {

    @GET("teams")
    suspend fun getTeams(
        @Header("x-apisports-key") apiKey: String,
        @Query("league") league: Int,
        @Query("season") season: Int
    ): TeamResponseDto

    @GET("players")
    suspend fun getPlayers(
        @Header("x-apisports-key") apiKey: String,
        @Query("team") team: Int,
        @Query("season") season: Int
    ): PlayerResponseDto

    @GET("coachs")
    suspend fun getCoach(
        @Header("x-apisports-key") apiKey: String,
        @Query("team") team: Int
    ): CoachResponseDto

    @GET("players")
    suspend fun getPlayersByTeamAndPage(
        @Header("x-apisports-key") apiKey: String,
        @Query("team") team: Int,
        @Query("season") season: Int,
        @Query("page") page: Int
    ): PlayerResponseDto
}
