package com.album.figurinha.ui.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Album : Routes("album")
    object SelectionDetail : Routes("selection_detail/{teamId}") {
        fun createRoute(teamId: Int) = "selection_detail/$teamId"
    }
    object PlayerDetail : Routes("player_detail/{playerId}") {
        fun createRoute(playerId: Int) = "player_detail/$playerId"
    }
    object CoachDetail : Routes("coach_detail/{coachId}") {
        fun createRoute(coachId: Int) = "coach_detail/$coachId"
    }
    object CountryDetail : Routes("country_detail/{teamId}") {
        fun createRoute(teamId: Int) = "country_detail/$teamId"
    }
    object Store : Routes("store")
}