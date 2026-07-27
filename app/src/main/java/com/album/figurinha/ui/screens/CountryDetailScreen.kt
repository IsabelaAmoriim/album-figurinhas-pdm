package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.*
import java.util.*

@Composable
fun CountryDetailScreen(teamId: Int, onBack: () -> Unit) {
    val teamName = when(teamId) {
        1 -> "Brasil"
        2 -> "Argentina"
        3 -> "França"
        else -> "Portugal"
    }
    val teamColor = when(teamId) {
        1 -> BrazilGreen
        2 -> ArgentinaBlue
        3 -> FranceBlue
        else -> Color(0xFFE42518)
    }
    val shieldUrl = when(teamId) {
        1 -> "https://media.api-sports.io/football/teams/6.png"
        2 -> "https://media.api-sports.io/football/teams/26.png"
        3 -> "https://media.api-sports.io/football/teams/2.png"
        else -> "https://media.api-sports.io/football/teams/27.png"
    }
    
    val confederation = if (teamId == 1 || teamId == 2) "CONMEBOL" else "UEFA"

    // Mock country sticker as a special Player object for the card component
    val countrySticker = Player(id = 999, name = teamName, photo = shieldUrl, number = teamId, position = "NAÇÃO", description = "", teamId = teamId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = teamName.take(2).uppercase(Locale.getDefault()),
                            color = teamColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = teamName.uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "INFORMAÇÕES TÉCNICAS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("📊", "#${if (teamId == 1) 3 else if (teamId == 2) 1 else 2}", "RANKING FIFA", Modifier.weight(1f))
                InfoCard("🌍", if (teamId == 1) "22" else "19", "PARTICIPAÇÕES", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("🏆", if (teamId == 1) "5" else if (teamId == 2) "3" else "2", "TÍTULOS", Modifier.weight(1f))
                InfoCard("🏙️", if (teamId == 1) "Brasília" else if (teamId == 2) "B. Aires" else "Paris", "CAPITAL", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mythic Country Sticker Section
            Text(text = "FIGURINHA COLECIONÁVEL", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StickerCard(
                    player = countrySticker,
                    isCollected = true,
                    rarity = StickerRarity.MYTHIC,
                    teamColor = teamColor,
                    modifier = Modifier.scale(1.1f) // Slightly larger for emphasis
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "HISTÓRIA E CURIOSIDADES", fontWeight = FontWeight.Black, color = teamColor, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when(teamId) {
                            1 -> "O Brasil é a única seleção a participar de todas as Copas do Mundo. Com o estilo 'Joga Bonito', encantou o mundo em 1970 e busca o hexacampeonato em 2026."
                            2 -> "A Argentina vive uma era de ouro após o título de 2022. Com uma torcida apaixonada e um legado de craques, busca manter sua hegemonia mundial."
                            else -> "Portugal consolidou-se como uma das potências europeias. Com uma nova geração de talentos, a seleção lusa busca seu primeiro título mundial."
                        },
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

// Helper to scale components slightly
@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

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
            Text(text = icon, fontSize = 24.sp)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(text = label, fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}