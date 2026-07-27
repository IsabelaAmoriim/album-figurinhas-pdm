package com.album.figurinha.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.album.figurinha.model.Player
import com.album.figurinha.ui.components.CoinWallet
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.DarkBlueBg
import com.album.figurinha.ui.theme.WorldCupGold
import com.album.figurinha.ui.theme.WorldCupYellow
import com.album.figurinha.viewmodel.PackViewModel
import com.album.figurinha.viewmodel.WalletViewModel

@Composable
fun StoreScreen(
    walletViewModel: WalletViewModel,
    packViewModel: PackViewModel,
    onBack: () -> Unit
) {
    val walletState by walletViewModel.wallet.collectAsState()
    val newStickers by packViewModel.newStickers.collectAsState()
    val isOpening by packViewModel.isOpening.collectAsState()
    
    // Track set of revealed indices
    var revealedIndices by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(newStickers) {
        if (newStickers.isEmpty()) revealedIndices = emptySet()
    }

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
            Text(text = "PACK SHOP", color = WorldCupGold, fontWeight = FontWeight.Black, fontSize = 20.sp)
            CoinWallet(balance = walletState.moedas)
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (newStickers.isEmpty()) {
            PremiumPackView()

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { packViewModel.openPack(walletViewModel) },
                enabled = !isOpening && walletState.moedas >= 20,
                colors = ButtonDefaults.buttonColors(containerColor = WorldCupYellow),
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isOpening) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "GET PACK (20 COINS)", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        } else {
            Text(
                text = if (revealedIndices.size < 5) "REVEAL ALL 5 STICKERS!" else "PACK COMPLETE!",
                color = WorldCupYellow,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(30.dp))

            // Professional Dice Layout
            Box(modifier = Modifier.fillMaxWidth().height(420.dp), contentAlignment = Alignment.Center) {
                if (newStickers.size >= 5) {
                    // Top Row
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DiceCard(player = newStickers[0], isRevealed = revealedIndices.contains(0)) { revealedIndices = revealedIndices + 0 }
                        DiceCard(player = newStickers[1], isRevealed = revealedIndices.contains(1)) { revealedIndices = revealedIndices + 1 }
                    }
                    
                    // Middle
                    DiceCard(player = newStickers[2], isRevealed = revealedIndices.contains(2)) { revealedIndices = revealedIndices + 2 }
                    
                    // Bottom Row
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DiceCard(player = newStickers[3], isRevealed = revealedIndices.contains(3)) { revealedIndices = revealedIndices + 3 }
                        DiceCard(player = newStickers[4], isRevealed = revealedIndices.contains(4)) { revealedIndices = revealedIndices + 4 }
                    }
                }
            }
            
            if (revealedIndices.size == 5) {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { packViewModel.clearNewStickers() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "COLLECT ALL", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (newStickers.isEmpty()) {
            TextButton(onClick = onBack) {
                Text(text = "Return to Album", color = Color.Gray)
            }
        }
    }
}

@Composable
fun PremiumPackView() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = Modifier
            .size(230.dp, 320.dp)
            .shadow(32.dp, RoundedCornerShape(24.dp), spotColor = WorldCupGold),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black,
        border = androidx.compose.foundation.BorderStroke(2.dp, Brush.linearGradient(listOf(WorldCupGold, Color.Black)))
    ) {
        Box(modifier = Modifier.background(
            Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color.Black))
        )) {
            // Foil Shine Effect
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = shimmerAlpha),
                    0.5f to Color.Transparent,
                    1.0f to Color.White.copy(alpha = shimmerAlpha)
                )
            ))
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "2026", color = WorldCupGold, fontSize = 72.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                Text(text = "PREMIUM EDITION", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Surface(
                    color = WorldCupYellow,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "5 COLLECTIBLES",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DiceCard(
    player: Player, 
    isRevealed: Boolean, 
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(105.dp, 145.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 15f * density
                scaleX = if (isRevealed) 1.15f else 1.0f
                scaleY = if (isRevealed) 1.15f else 1.0f
            }
            .clickable(enabled = !isRevealed, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = WorldCupYellow,
                border = androidx.compose.foundation.BorderStroke(3.dp, Color.Black)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "FIFA", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
                }
            }
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                StickerCard(
                    player = player,
                    isCollected = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}