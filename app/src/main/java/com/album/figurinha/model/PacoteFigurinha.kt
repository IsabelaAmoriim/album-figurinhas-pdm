package com.album.figurinha.model

import java.time.LocalDateTime

data class PacoteFigurinha(
    val id: Int,
    val openDate: LocalDateTime,
    val stickers: List<Figurinha> = emptyList()
)