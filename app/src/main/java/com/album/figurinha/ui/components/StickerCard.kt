package com.album.figurinha.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.ImageMapper
import com.album.figurinha.util.StickerImageResolver

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
        StickerRarity.MYTHIC -> Color(0xFFFF4500)
        StickerRarity.LEGENDARY -> WorldCupGold
        StickerRarity.COACHING -> Color(0xFF444444) // Metallic Grey
        StickerRarity.SPECIAL -> Color(0xFFC0C0C0)
        else -> teamColor.copy(alpha = 0.15f)
    }

    val backgroundBrush = if (isCollected) {
        when (rarity) {
            StickerRarity.MYTHIC -> Brush.verticalGradient(listOf(Color(0xFF3B0B00), CardBackground))
            StickerRarity.COACHING -> Brush.verticalGradient(listOf(Color(0xFF1C1C1C), Color.Black))
            StickerRarity.LEGENDARY -> Brush.verticalGradient(listOf(WorldCupGold.copy(alpha = 0.05f), CardBackground))
            else -> Brush.verticalGradient(listOf(CardBackground, CardBackground))
        }
    } else {
        Brush.verticalGradient(listOf(CardBackground, CardBackground))
    }

    val finalImageUrl = player?.let { 
        StickerImageResolver.getPlayerImageUrl(it.id, it.photo)
    }
    
    // Pure silhouette effect for locked stickers
    val silhouetteFilter = remember {
        val matrix = ColorMatrix().apply {
            setToSaturation(0f)
            this[0, 0] = 0f; this[0, 1] = 0f; this[0, 2] = 0f; this[0, 3] = 0f; this[0, 4] = 40f
            this[1, 0] = 0f; this[1, 1] = 0f; this[1, 2] = 0f; this[1, 3] = 0f; this[1, 4] = 40f
            this[2, 0] = 0f; this[2, 1] = 0f; this[2, 2] = 0f; this[2, 3] = 0f; this[2, 4] = 40f
        }
        ColorFilter.colorMatrix(matrix)
    }

    val finalShieldUrl = teamShield?.let {
        StickerImageResolver.getTeamShieldUrl(player?.teamId ?: 0, it)
    }

    val localImage = player?.let { ImageMapper.getLocalPlayerImage(it.id) }
    val localShield = player?.let { ImageMapper.getLocalTeamShield(it.teamId) }

    Surface(
        modifier = modifier
            .width(160.dp) 
            .height(220.dp) 
            .shadow(
                elevation = if (isCollected && (isMythic || rarity == StickerRarity.LEGENDARY)) 10.dp else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isMythic) Color(0xFFFF4500) else borderColor
            )
            .border(
                width = if (rarity != StickerRarity.COMMON) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.background(backgroundBrush)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(if (isCollected) Color.Transparent else Color.DarkGray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (player != null) {
                    SubcomposeAsyncImage(
                        model = localImage ?: finalImageUrl,
                        contentDescription = player.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = if (isCollected) null else silhouetteFilter,
                        loading = {
                            ShimmerLoadingEffect(borderColor)
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(55.dp),
                                tint = teamColor.copy(alpha = 0.25f)
                            )
                        }
                    )
                    
                    if (isCollected && (finalShieldUrl != null || localShield != null)) {
                        SubcomposeAsyncImage(
                            model = localShield ?: finalShieldUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.85f), CircleShape)
                                .padding(4.dp)
                        )
                    }

                    if (isCollected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .size(24.dp)
                                .background(if (isMythic) Color(0xFFFF4500) else WorldCupYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${player.number}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                } else {
                    Text(text = "?", fontSize = 44.sp, color = Color.White.copy(alpha = 0.04f), fontWeight = FontWeight.Black)
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (isCollected) player?.name?.uppercase() ?: "???" else "???",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = if (isCollected) rarity.label else "LOCKED",
                    color = if (isCollected) borderColor else Color.Gray,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ShimmerLoadingEffect(color: Color) {
    val shimmerColors = listOf(
        color.copy(alpha = 0.05f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.05f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )

    Box(modifier = Modifier.fillMaxSize().background(brush))
}