package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.album.figurinha.model.PlayerDetails
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.DataViewModel

@Composable
fun PlayerDetailScreen(
    teamId: Int,
    playerId: Int,
    dataViewModel: DataViewModel,
    onBack: () -> Unit
) {
    var details by remember { mutableStateOf<PlayerDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(playerId) {
        isLoading = true
        dataViewModel.getPlayerDetails(teamId, playerId) { result ->
            details = result
            isLoading = false
        }
    }

    val player = details ?: PlayerDetails(
        id = playerId,
        name = "Jogador",
        photo = "",
        number = 0,
        position = "POSIÇÃO",
        teamId = teamId
    )

    val teamColor = resolveTeamColor(player.teamId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(teamColor)
        ) {
            if (player.photo.isNotEmpty()) {
                AsyncImage(
                    model = player.photo,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(player.name, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Text(
                        "${player.position} · ${player.nationality ?: ""}",
                        color = teamColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    color = teamColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Nº ${player.number}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = teamColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("ESTATÍSTICAS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    value = player.appearances?.toString() ?: "-",
                    label = "JOGOS",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = player.goals?.toString() ?: "-",
                    label = "GOLS",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = player.assists?.toString() ?: "-",
                    label = "ASSIST.",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    value = player.rating?.toString() ?: "-",
                    label = "NOTA",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = player.minutes?.toString() ?: "-",
                    label = "MIN.",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = player.passesKey?.toString() ?: "-",
                    label = "PASSES",
                    modifier = Modifier.weight(1f)
                )
            }

            if (!isLoading && player.goals != null) {
                Spacer(modifier = Modifier.height(32.dp))

                val rarityLabel = when {
                    (player.goals ?: 0) >= 7 -> "Figurinha Mítica"
                    (player.goals ?: 0) >= 4 -> "Figurinha Lendária"
                    else -> "Figurinha Especial"
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if ((player.goals ?: 0) >= 7) WorldCupGold else teamColor,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = WorldCupYellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(rarityLabel, color = Color.Black, fontWeight = FontWeight.Bold)
                            Text(
                                "Copa do Mundo 2022 · Edição Limitada",
                                color = Color.Black.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
