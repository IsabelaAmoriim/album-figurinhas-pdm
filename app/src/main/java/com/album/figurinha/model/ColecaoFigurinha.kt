package com.album.figurinha.model

data class ColecaoFigurinha(
    val id: Int,
    val stickerId: Int,
    val quantity: Int,
    val unlocked: Boolean,
    val repeated: Boolean
)