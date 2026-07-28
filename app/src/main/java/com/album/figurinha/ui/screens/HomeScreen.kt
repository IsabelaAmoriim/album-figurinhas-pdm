package com.album.figurinha.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.ui.components.CoinWallet
import com.album.figurinha.ui.theme.*
import com.album.figurinha.util.ConnectivityObserver
import com.album.figurinha.viewmodel.AlbumProgress

@Composable
fun HomeScreen(
    balance: Int,
    progress: AlbumProgress,
    selections: List<CatalogSticker>,
    recompensasDisponiveis: Boolean = false,
    onClaimReward: () -> Unit = {},
    networkStatus: ConnectivityObserver.Status,
    onSelectionClick: (Int) -> Unit,
    onStoreClick: () -> Unit,
    onAlbumClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                border = BorderStroke(1.dp, WorldCupGold)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
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
                            Text("Recompensa Diária", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("+50 moedas grátis!", color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = WorldCupYellow),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("RESGATAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                    Text("Collection Progress", color = Color.White, fontSize = 12.sp)
                    Text(
                        "${progress.percentage}% (${progress.collected}/${progress.total})",
                        color = WorldCupGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.fraction },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = WorldCupGold,
                    trackColor = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("World Cup", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)

        Surface(
            color = WorldCupYellow,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                "2022",
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
            Text("GET STICKER PACKS", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onAlbumClick,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, WorldCupGold),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WorldCupGold),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("MEU ÁLBUM", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text(" SELECTIONS ", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            itemsIndexed(selections) { index, selection ->
                AnimatedSelectionItem(
                    name = selection.name,
                    shieldUrl = selection.imageUrl,
                    color = selectionColor(selection.teamId),
                    index = index,
                    onClick = { onSelectionClick(selection.teamId) }
                )
            }
        }
    }
}

fun selectionColor(teamId: Int): Color = when (teamId) {
    1 -> BrazilGreen
    2 -> ArgentinaBlue
    3 -> FranceBlue
    4 -> PortugalRed
    5 -> Color(0xFFE42518)  // Spain
    6 -> Color(0xFF1D428A)  // Netherlands
    else -> WorldCupGold
}

@Composable
fun AnimatedSelectionItem(
    name: String,
    shieldUrl: String,
    color: Color,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 50 * (index + 1) },
            animationSpec = tween(500, delayMillis = 100 * index)
        ) + fadeIn(animationSpec = tween(500, delayMillis = 100 * index))
    ) {
        SelectionItem(name = name, shieldUrl = shieldUrl, color = color, onClick = onClick)
    }
}

@Composable
fun SelectionItem(
    name: String,
    shieldUrl: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = shieldUrl,
                    contentDescription = name,
                    modifier = Modifier.size(50.dp),
                    loading = { CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1226)
@Composable
private fun SelectionItemPreview() {
    FigurinhaTheme {
        SelectionItem(name = "Brasil", shieldUrl = "", color = BrazilGreen, onClick = {})
    }
}
