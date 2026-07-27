package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.StickerImageResolver
import java.util.*

@Composable
fun SelectionDetailScreen(
    teamId: Int,
    onBack: () -> Unit,
    onCountryClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onCoachClick: (Int) -> Unit
) {
    // Resolved Data
    val teamName = when(teamId) {
        1 -> "BRASIL"
        2 -> "ARGENTINA"
        3 -> "FRANÇA"
        else -> "PORTUGAL"
    }
    val teamColor = when(teamId) {
        1 -> BrazilGreen
        2 -> ArgentinaBlue
        3 -> FranceBlue
        else -> Color(0xFFE42518)
    }
    val resolvedShield = StickerImageResolver.getTeamShieldUrl(teamId, "")
    val resolvedFlag = StickerImageResolver.getCountryFlagUrl(teamId)

    val players = listOf(
        Player(614, "Neymar", "", 10, "ATACANTE", "...", 1),
        Player(732, "Vinícius Jr", "", 7, "ATACANTE", "...", 1),
        Player(154, "Lionel Messi", "", 10, "ATACANTE", "...", 2),
        Player(474, "E. Martínez", "", 23, "GOLEIRO", "...", 2),
        Player(276, "K. Mbappé", "", 10, "ATACANTE", "...", 3),
        Player(874, "C. Ronaldo", "", 7, "ATACANTE", "...", 4)
    ).filter { it.teamId == teamId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(teamColor)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }

                TextButton(
                    onClick = { onCountryClick(teamId) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Text(text = "About Country")
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                        SubcomposeAsyncImage(
                            model = resolvedShield, 
                            contentDescription = null,
                            loading = { CircularProgressIndicator(color = teamColor) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = teamName, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    repeat(if (teamId == 1) 5 else if (teamId == 2) 3 else if (teamId == 3) 2 else 0) {
                        Icon(Icons.Default.Star, null, tint = WorldCupYellow, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "ABOUT SELECTION", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = teamColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The ${teamName.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }} National Team is a global reference in football...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = teamColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubcomposeAsyncImage(
                        model = resolvedFlag,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Collected Stickers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    LinearProgressIndicator(
                        progress = { 0.2f },
                        modifier = Modifier.width(100.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = teamColor
                    )
                    Text(text = " ${players.size}/11", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "COACHING STAFF", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkBlueBg,
                shape = RoundedCornerShape(16.dp),
                onClick = { onCoachClick(if (teamId == 1) 1 else 2) }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = if (teamId == 1) "Carlo Ancelotti" else if (teamId == 2) "Lionel Scaloni" else "Head Coach", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Technical Lead", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WorldCupYellow)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PLAYER LIST",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                players.chunked(2).forEach { rowPlayers ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowPlayers.forEach { player ->
                            // For testing silhouettes, let's make Neymar "uncollected" if he's ID 614
                            val isCollected = player.id != 614 
                            
                            StickerCard(
                                player = player,
                                isCollected = isCollected,
                                teamColor = teamColor,
                                teamShield = resolvedShield,
                                rarity = if (player.id == 154 || player.id == 874) StickerRarity.LEGENDARY else StickerRarity.COMMON,
                                modifier = Modifier.weight(1f).clickable { onPlayerClick(player.id) }
                            )
                        }
                        if (rowPlayers.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}