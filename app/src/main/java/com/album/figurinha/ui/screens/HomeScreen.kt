package com.album.figurinha.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.ui.components.CoinWallet
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.ConnectivityObserver
import com.album.figurinha.util.StickerImageResolver

@Composable
fun HomeScreen(
    balance: Int, 
    recompensasDisponiveis: Boolean = false,
    onClaimReward: () -> Unit = {},
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

        Spacer(modifier = Modifier.height(16.dp))

        if (recompensasDisponiveis) {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorldCupGold)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = WorldCupYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Recompensa Diária", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "+50 moedas grátis!", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = WorldCupYellow),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = "RESGATAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                    Text(text = "Collection Progress", color = Color.White, fontSize = 12.sp)
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
            Text(text = "GET STICKER PACKS", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text(
                text = " SELECTIONS ",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selections = listOf(
            Triple("Brasil", 1, BrazilGreen),
            Triple("Argentina", 2, ArgentinaBlue),
            Triple("França", 3, FranceBlue),
            Triple("Portugal", 4, Color(0xFFE42518))
        )

        // Selections List with Entry Animations
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            itemsIndexed(selections) { index, item ->
                val resolvedShield = StickerImageResolver.getTeamShieldUrl(item.second, "")
                AnimatedSelectionItem(
                    name = item.first,
                    shieldUrl = resolvedShield,
                    color = item.third,
                    index = index,
                    onClick = { onSelectionClick(item.second) }
                )
            }
        }
    }
}

@Composable
fun AnimatedSelectionItem(name: String, shieldUrl: String, color: Color, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 50 * (index + 1) },
            animationSpec = tween(durationMillis = 500, delayMillis = 100 * index)
        ) + fadeIn(animationSpec = tween(500, delayMillis = 100 * index))
    ) {
        SelectionItem(name, shieldUrl, color, if (index == 3) 0 else 5 - index, onClick)
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
                    .size(70.dp) // Increased size for the shield container
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = shieldUrl,
                    contentDescription = name,
                    modifier = Modifier.size(50.dp), // Increased shield size
                    loading = { CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (titles > 0) {
                        repeat(titles) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = WorldCupYellow
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$titles titles", color = Color.Gray, fontSize = 11.sp)
                    } else {
                        Text(text = "Hunting for 1st title", color = Color.Gray, fontSize = 11.sp)
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