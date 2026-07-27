package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.StickerImageResolver

@Composable
fun PlayerDetailScreen(playerId: Int, onBack: () -> Unit) {
    val basePlayer = com.album.figurinha.repository.PlayersData.getPlayerById(playerId)
        ?: Player(playerId, "Jogador", "", 0, "POSIÇÃO", "...", 1)
    
    val player = basePlayer.copy(photo = StickerImageResolver.getPlayerImageUrl(basePlayer.id, basePlayer.photo))
    
    val teamColor = when(player.teamId) {
        1 -> BrazilGreen
        2 -> ArgentinaBlue
        3 -> FranceBlue
        else -> Color(0xFFE42518)
    }

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
            AsyncImage(
                model = player.photo,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
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
                    Text(
                        text = player.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Text(
                        text = "${player.position} · ${if (player.teamId == 1) "BRASIL" else "ARGENTINA"}",
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
                            text = "Nº ${player.number}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = teamColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "ESTATÍSTICAS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(value = if (playerId == 3) "100" else "51", label = "JOGOS", modifier = Modifier.weight(1f))
                StatCard(value = if (playerId == 3) "800" else if (playerId == 1) "79" else "0", label = "GOLS", modifier = Modifier.weight(1f))
                StatCard(value = if (playerId == 3) "350" else "0", label = "ASSIST.", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (player.id == 3) WorldCupGold else teamColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = WorldCupYellow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (player.id == 3) "Figurinha Lendária" else "Figurinha Especial",
                            color = if (player.id == 3) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (player.teamId == 1) "Brasil" else "Argentina"} · Copa do Mundo 2026 · Edição Limitada",
                            color = (if (player.id == 3) Color.Black else Color.White).copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
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
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}