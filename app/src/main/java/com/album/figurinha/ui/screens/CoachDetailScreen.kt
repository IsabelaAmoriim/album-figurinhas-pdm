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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.Coach
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.DataViewModel

@Composable
fun CoachDetailScreen(
    coachId: Int,
    dataViewModel: DataViewModel,
    onBack: () -> Unit
) {
    // Procura o coach em todos os times carregados
    val allTeams = dataViewModel.state.value.teams
    val coachEntry = remember(coachId, allTeams) {
        allTeams.firstNotNullOfOrNull { team ->
            team.coach?.takeIf { it.id == coachId }?.let { it to team }
        }
    }
    val coach = coachEntry?.first
    val team = coachEntry?.second

    val coachName = coach?.name ?: "Técnico"
    val teamName = team?.name?.uppercase() ?: "SELEÇÃO"
    val teamColor = if (team != null) resolveTeamColor(team.id) else WorldCupGold
    val coachPhoto = coach?.photo ?: ""

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
            if (coachPhoto.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = coachPhoto,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, DarkBlueBg)))
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
                    Text(coachName, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("TREINADOR · $teamName", color = WorldCupYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        Text("Figurinha Corpo Técnico", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Raridade Exclusiva · $teamName", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("INFORMAÇÕES", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (coach != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CoachStatCard(coach.description.ifEmpty { "-" }, "NACIONALIDADE", Modifier.weight(1f))
                    CoachStatCard("TREINADOR", "CARGO", Modifier.weight(1f))
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CoachStatCard("-", "NACIONALIDADE", Modifier.weight(1f))
                    CoachStatCard("-", "CARGO", Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CoachStatCard(value: String, label: String, modifier: Modifier = Modifier) {
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
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
