package com.album.figurinha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.theme.*

@Composable
fun StickerCard(
    player: Player?,
    isCollected: Boolean,
    modifier: Modifier = Modifier,
    teamColor: Color = BrazilGreen,
    teamShield: String? = null,
    rarity: StickerRarity = StickerRarity.COMMON
) {
    val isMythic = rarity == StickerRarity.MYTHIC
    
    val borderColor = when (rarity) {
        StickerRarity.MYTHIC -> teamColor.copy(alpha = 0.7f)
        StickerRarity.LEGENDARY -> WorldCupGold
        StickerRarity.COACHING -> Color.Black
        StickerRarity.SPECIAL -> Color(0xFFC0C0C0)
        else -> teamColor.copy(alpha = 0.3f)
    }

    val backgroundBrush = if (isCollected) {
        when (rarity) {
            StickerRarity.MYTHIC -> Brush.verticalGradient(listOf(teamColor.copy(alpha = 0.2f), CardBackground))
            StickerRarity.COACHING -> Brush.verticalGradient(listOf(Color(0xFF2C2C2C), Color.Black))
            StickerRarity.LEGENDARY -> Brush.verticalGradient(listOf(WorldCupGold.copy(alpha = 0.1f), CardBackground))
            else -> Brush.verticalGradient(listOf(CardBackground, CardBackground))
        }
    } else {
        Brush.verticalGradient(listOf(CardBackground, CardBackground))
    }

    Surface(
        modifier = modifier
            .width(if (isMythic) 200.dp else 160.dp) // Mythic is wider
            .height(if (isMythic) 180.dp else 220.dp) // Mythic is shorter/landscape style
            .shadow(
                elevation = if (isCollected && (isMythic || rarity == StickerRarity.LEGENDARY)) 12.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = borderColor
            )
            .border(
                width = if (rarity != StickerRarity.COMMON) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.background(backgroundBrush)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(if (isCollected) Color.Transparent else Color.DarkGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (isCollected && player != null) {
                    SubcomposeAsyncImage(
                        model = player.photo,
                        contentDescription = player.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = borderColor, strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            // Offline Fallback Visual
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = teamColor.copy(alpha = 0.4f)
                            )
                        }
                    )
                    
                    if (teamShield != null) {
                        SubcomposeAsyncImage(
                            model = teamShield,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .size(30.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                .padding(4.dp)
                        )
                    }

                    if (!isMythic) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(26.dp)
                                .background(WorldCupYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${player.number}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                } else {
                    Text(text = "?", fontSize = 48.sp, color = Color.White.copy(alpha = 0.1f), fontWeight = FontWeight.Black)
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (isCollected) player?.name?.uppercase() ?: "???" else "???",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = if (isMythic) 16.sp else 13.sp,
                    maxLines = 1
                )
                Text(
                    text = if (isCollected) rarity.label else "COLECIONÁVEL",
                    color = if (isCollected) borderColor else Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}