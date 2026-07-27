package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    val resolvedShield = StickerImageResolver.getTeamShieldUrl(teamId, "")
    val resolvedFlag = StickerImageResolver.getCountryFlagUrl(teamId)
    
    val countrySticker = Player(id = 999, name = teamName, photo = resolvedFlag, number = teamId, position = "NAÇÃO", description = "", teamId = teamId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
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
                Text(text = teamName.uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "TECHNICAL INFO", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("📊", "#${if (teamId == 1) 3 else if (teamId == 2) 1 else 2}", "FIFA RANK", Modifier.weight(1f))
                InfoCard("🌍", if (teamId == 1) "22" else "19", "APPEARANCES", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard("🏆", if (teamId == 1) "5" else if (teamId == 2) "3" else "2", "TITLES", Modifier.weight(1f))
                InfoCard("🏙️", if (teamId == 1) "Brasília" else if (teamId == 2) "B. Aires" else "Paris", "CAPITAL", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Mythic Country Sticker Section
            Text(text = "MYTHIC COLLECTIBLE", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StickerCard(
                    player = countrySticker,
                    isCollected = true,
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
                    Text(text = "STORY & FACTS", fontWeight = FontWeight.Black, color = teamColor, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when(teamId) {
                            1 -> "Brazil is the only nation to have played in every World Cup. Known for 'Joga Bonito', the team has won a record five titles and continues to be a global football powerhouse."
                            2 -> "Argentina is currently experiencing a golden era after their 2022 victory. With a legacy of legends like Maradona and Messi, they seek to maintain world dominance."
                            else -> "Portugal has emerged as a major European force. With a new generation of world-class talent, the 'Seleção das Quinas' is hunting for its first world title."
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