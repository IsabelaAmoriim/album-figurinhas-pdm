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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.theme.*

@Composable
fun CoachDetailScreen(coachId: Int, onBack: () -> Unit) {
    val coachName = when (coachId) {
        1 -> "Carlo Ancelotti"
        2 -> "Lionel Scaloni"
        else -> "Didier Deschamps"
    }
    val teamName = when(coachId) {
        1 -> "BRASIL"
        2 -> "ARGENTINA"
        else -> "FRANÇA"
    }
    val teamColor = when(coachId) {
        1 -> BrazilGreen
        2 -> ArgentinaBlue
        else -> FranceBlue
    }
    val coachPhoto = when(coachId) {
        1 -> "https://media.api-sports.io/football/coachs/2.png"
        2 -> "https://media.api-sports.io/football/coachs/18.png"
        else -> "https://media.api-sports.io/football/coachs/10.png"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBg)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            SubcomposeAsyncImage(
                model = coachPhoto,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, DarkBlueBg)
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = coachName, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(text = "TREINADOR · $teamName", color = WorldCupYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Coaching Rarity Badge (Black)
            Surface(
                color = StickerRarity.COACHING.color,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).background(teamColor, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Figurinha Corpo Técnico", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Raridade Exclusiva · $teamName", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "TÍTULOS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TrophyStat("1x", "COPA", Modifier.weight(1f))
                TrophyStat(if (coachId == 2) "2x" else "0x", "AMÉRICA", Modifier.weight(1f))
                TrophyStat(if (coachId == 2) "1x" else "0x", "FINALÍSS.", Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TrophyStat(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(90.dp),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}