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
import coil.compose.AsyncImage
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.*
import java.util.*

@Composable
fun SelectionDetailScreen(
    teamId: Int,
    onBack: () -> Unit,
    onCountryClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onCoachClick: (Int) -> Unit
) {
    // Mock Data for now
    val teamName = when(teamId) {
        1 -> "BRASIL"
        2 -> "ARGENTINA"
        else -> "FRANÇA"
    }
    val teamColor = when(teamId) {
        1 -> BrazilGreen
        2 -> ArgentinaBlue
        else -> FranceBlue
    }
    val shieldUrl = when(teamId) {
        1 -> "https://media.api-sports.io/football/teams/6.png"
        2 -> "https://media.api-sports.io/football/teams/26.png"
        else -> "https://media.api-sports.io/football/teams/2.png"
    }

    val players = listOf(
        Player(1, "Neymar", "https://media.api-sports.io/football/players/614.png", 10, "ATACANTE", "...", 1),
        Player(2, "Vinícius Jr", "https://media.api-sports.io/football/players/732.png", 7, "ATACANTE", "...", 1),
        Player(3, "Lionel Messi", "https://media.api-sports.io/football/players/154.png", 10, "ATACANTE", "...", 2),
        Player(23, "E. Martínez", "https://media.api-sports.io/football/players/474.png", 23, "GOLEIRO", "...", 2)
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
                    Text(text = "Sobre o país")
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                        AsyncImage(model = shieldUrl, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = teamName, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    repeat(if (teamId == 1) 5 else if (teamId == 2) 3 else 2) {
                        Icon(Icons.Default.Star, null, tint = WorldCupYellow, modifier = Modifier.size(20.dp))
                    }
                }
                Text(text = "${if (teamId == 1) 5 else if (teamId == 2) 3 else 2} Títulos Mundiais", color = Color.White, fontSize = 12.sp)
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
                    Text(text = "SOBRE A SELEÇÃO", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = teamColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A Seleção ${teamName.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }} de Futebol é uma referência global...",
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
                    Text(text = "Figurinhas Coletadas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    LinearProgressIndicator(
                        progress = { 0.5f },
                        modifier = Modifier.width(100.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = teamColor
                    )
                    Text(text = " ${players.size}/11", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "COMISSÃO TÉCNICA", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text(text = if (teamId == 1) "Ancelotti" else "Scaloni", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Treinador Principal", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WorldCupYellow)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ELENCO",
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
                            StickerCard(
                                player = player,
                                isCollected = true,
                                teamColor = teamColor,
                                teamShield = shieldUrl,
                                rarity = if (player.id == 3) StickerRarity.LEGENDARY else StickerRarity.COMMON,
                                modifier = Modifier.weight(1f).clickable { onPlayerClick(player.id) }
                            )
                        }
                        if (rowPlayers.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}