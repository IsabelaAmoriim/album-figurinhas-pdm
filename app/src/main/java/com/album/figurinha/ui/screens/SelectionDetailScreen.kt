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
import androidx.compose.runtime.*
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
import com.album.figurinha.model.Team
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.DataViewModel

@Composable
fun SelectionDetailScreen(
    team: Team?,
    teamId: Int,
    collectedIds: Set<Int> = emptySet(),
    raritiesMap: Map<Int, StickerRarity> = emptyMap(),
    dataViewModel: DataViewModel,
    onBack: () -> Unit,
    onCountryClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onCoachClick: (Int) -> Unit
) {
    val teamName = team?.name?.uppercase() ?: "SELEÇÃO"
    val teamColor = resolveTeamColor(teamId)
    val resolvedShield = team?.shield ?: ""
    val resolvedFlag = "https://cdn.sofifa.net/flags/${resolveCountryCode(teamId)}@3x.png"
    val players = dataViewModel.getPlayersForTeam(teamId)
    val coach = dataViewModel.getCoachForTeam(teamId)

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }

                TextButton(
                    onClick = { onCountryClick(teamId) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Text("About Country")
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
                Text(teamName, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
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
                    Text("ABOUT SELECTION", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = teamColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The ${team?.name ?: "National Team"} participated in the FIFA World Cup 2022 in Qatar.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    val collectedCount = players.count { it.id in collectedIds }
                    val totalCount = players.size
                    val teamProgress = if (totalCount > 0) collectedCount.toFloat() / totalCount else 0f

                    Text("Collected Stickers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    LinearProgressIndicator(
                        progress = { teamProgress },
                        modifier = Modifier.width(100.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = teamColor
                    )
                    Text(" $collectedCount/$totalCount", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (coach != null) {
                Text("COACHING STAFF", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBlueBg,
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onCoachClick(coach.id) }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray)) {
                            SubcomposeAsyncImage(
                                model = coach.photo,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                loading = { CircularProgressIndicator(modifier = Modifier.size(20.dp)) }
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(coach.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(coach.role, color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.Star, null, tint = WorldCupYellow)
                    }
                }
            } else {
                Text("COACHING STAFF", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBlueBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Técnico", color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            Text("Dados indisponíveis", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "PLAYER LIST",
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
                            val isCollected = player.id in collectedIds
                            StickerCard(
                                player = player,
                                isCollected = isCollected,
                                teamColor = teamColor,
                                teamShield = resolvedShield,
                                rarity = player.rarity,
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

fun resolveTeamColor(teamId: Int): Color = when (teamId) {
    1 -> BrazilGreen
    2 -> ArgentinaBlue
    3 -> FranceBlue
    4 -> PortugalRed
    else -> WorldCupGold
}

fun resolveCountryCode(teamId: Int): String = when (teamId) {
    1 -> "br"
    2 -> "ar"
    3 -> "fr"
    4 -> "pt"
    else -> "un"
}
