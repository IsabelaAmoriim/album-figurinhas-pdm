package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.ui.components.CoinWallet
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.ConnectivityObserver

@Composable
fun HomeScreen(
    balance: Int, 
    networkStatus: ConnectivityObserver.Status,
    onSelectionClick: (Int) -> Unit, 
    onStoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Wallet and Connectivity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FIFA",
                    color = WorldCupYellow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Discrete Connectivity Dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (networkStatus == ConnectivityObserver.Status.Available) Color.Green 
                            else Color.Red
                        )
                )
            }
            CoinWallet(balance = balance)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Bar Global
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardBackground,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Progresso do Álbum", color = Color.White, fontSize = 12.sp)
                    Text(text = "15%", color = WorldCupGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.15f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = WorldCupGold,
                    trackColor = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "World Cup",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black
        )
        Surface(
            color = WorldCupYellow,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "2026",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStoreClick,
            colors = ButtonDefaults.buttonColors(containerColor = WorldCupYellow),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "ABRIR PACOTES", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text(
                text = " SELEÇÕES ",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selections List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { 
                SelectionItem("Brasil", "https://media.api-sports.io/football/teams/6.png", BrazilGreen, 5) { 
                    onSelectionClick(1) 
                } 
            }
            item { 
                SelectionItem("Argentina", "https://media.api-sports.io/football/teams/26.png", ArgentinaBlue, 3) { 
                    onSelectionClick(2) 
                } 
            }
            item { 
                SelectionItem("França", "https://media.api-sports.io/football/teams/2.png", FranceBlue, 2) { 
                    onSelectionClick(3) 
                } 
            }
            item { 
                SelectionItem("Portugal", "https://media.api-sports.io/football/teams/27.png", Color(0xFFE42518), 0) { 
                    onSelectionClick(4) 
                } 
            }
        }
    }
}

@Composable
fun SelectionItem(name: String, shieldUrl: String, color: Color, titles: Int, onClick: () -> Unit) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = shieldUrl,
                    contentDescription = name,
                    modifier = Modifier.size(40.dp),
                    loading = { CircularProgressIndicator(color = Color.White) }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    if (titles > 0) {
                        repeat(titles) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = WorldCupYellow
                            )
                        }
                    } else {
                        Text(text = "Buscando o 1º título", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}