package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.repository.StickerCatalog
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.AlbumViewModel
import com.album.figurinha.viewmodel.DataViewModel

@Composable
fun CountryDetailScreen(
    teamId: Int,
    dataViewModel: DataViewModel,
    albumViewModel: AlbumViewModel,
    onBack: () -> Unit
) {
    val team = dataViewModel.getTeamById(teamId)
    val teamName = team?.name ?: "País"
    val teamColor = resolveTeamColor(teamId)
    val resolvedFlag = "https://cdn.sofifa.net/flags/${resolveCountryCode(teamId)}@3x.png"
    val teamShield = team?.shield ?: ""
    val players = dataViewModel.getPlayersForTeam(teamId)
    val playerCount = players.size

    val stickerId = StickerCatalog.selectionStickerId(teamId)
    val isCollected = albumViewModel.isCollected(stickerId)

    val countrySticker = Player(
        id = stickerId,
        name = teamName,
        photo = resolvedFlag,
        number = teamId,
        position = "NAÇÃO",
        description = "",
        teamId = teamId
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(teamColor)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(85.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                        SubcomposeAsyncImage(
                            model = resolvedFlag,
                            contentDescription = null,
                            loading = { CircularProgressIndicator(color = teamColor, strokeWidth = 2.dp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(teamName.uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text("TECHNICAL INFO", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("JOGADORES", "$playerCount", "PLAYERS", Modifier.weight(1f))
                InfoCard("COPA 2022", "Qatar", "HOST", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("SELEÇÃO", teamName.uppercase(), "TEAM", Modifier.weight(1f))
                InfoCard("STATUS", if (players.isNotEmpty()) "ATIVO" else "—", "SITUAÇÃO", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("MYTHIC COLLECTIBLE", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StickerCard(
                    player = countrySticker,
                    isCollected = isCollected,
                    rarity = StickerRarity.MYTHIC,
                    teamColor = teamColor,
                    modifier = Modifier.graphicsLayer(scaleX = 1.05f, scaleY = 1.05f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("WORLD CUP 2022", fontWeight = FontWeight.Black, color = teamColor, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "$teamName participated in the FIFA World Cup 2022 held in Qatar. " +
                                "The team's squad featured $playerCount players competing at the highest level of international football.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoCard(icon: String, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(label, fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
