package com.album.figurinha.model

data class Team(
    val id: Int,
    val name: String,
    val shield: String,
    val primaryColor: String,
    val secondaryColor: String,
    val description: String,
    val titles: Int,
    val players: List<Player> = emptyList(),
    val coach: Coach? = null
)

data class TeamItem(

    val team: Team

)

data class TeamResponse(

    val response: List<TeamItem>

)