package com.album.figurinha.model

import androidx.compose.ui.graphics.Color
import com.album.figurinha.ui.theme.WorldCupGold

enum class StickerRarity(val label: String, val color: Color) {
    COMMON("COMUM", Color.Gray),
    SPECIAL("ESPECIAL", Color(0xFFC0C0C0)), // Silver
    LEGENDARY("LENDÁRIA", WorldCupGold),
    MYTHIC("MÍTICA", Color(0xFFFF4500)), // Orange/Reddish Gold
    COACHING("CORPO TÉCNICO", Color.Black)
}