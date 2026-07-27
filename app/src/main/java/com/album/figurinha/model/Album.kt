package com.album.figurinha.model

data class Album(
    val id: Int,
    val totalStickers: Int,
    val collectedStickers: Int,
    val progress: Double,
    val stickers: List<ColecaoFigurinha> = emptyList(),
    val carteira: Carteira = Carteira()
)