package com.album.figurinha.util

import com.album.figurinha.R

object ImageMapper {
    // This map allows you to link IDs to local drawables
    // Since I can't upload PNGs, you can add your images to res/drawable
    // and map them here. Example: 1 to R.drawable.neymar
    private val playerImageMap = mapOf<Int, Int>(
        // 1 to R.drawable.player_1,
        // 3 to R.drawable.player_3,
    )

    private val teamShieldMap = mapOf<Int, Int>(
        // 1 to R.drawable.shield_brasil,
    )

    fun getLocalPlayerImage(playerId: Int): Int? = playerImageMap[playerId]
    fun getLocalTeamShield(teamId: Int): Int? = teamShieldMap[teamId]
}