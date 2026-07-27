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
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.components.CoinWallet
import com.album.figurinha.ui.components.StickerCard
import com.album.figurinha.ui.theme.DarkBlueBg
import com.album.figurinha.ui.theme.WorldCupGold
import com.album.figurinha.ui.theme.WorldCupYellow
import com.album.figurinha.ui.viewmodel.PackViewModel
import com.album.figurinha.ui.viewmodel.WalletViewModel

@Composable
fun StoreScreen(
    walletViewModel: WalletViewModel,
    packViewModel: PackViewModel,
    onBack: () -> Unit
) {
    val walletState by walletViewModel.wallet.collectAsState()
    val newStickers by packViewModel.newStickers.collectAsState()
    val isOpening by packViewModel.isOpening.collectAsState()
    
    var revealedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(newStickers) {
        if (newStickers.isEmpty()) revealedIndex = -1
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

        Spacer(modifier = Modifier.height(50.dp))

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
                text = if (revealedIndex == -1) "PICK YOUR LUCK!" else "LEGEND UNLOCKED!",
                color = WorldCupYellow,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                if (newStickers.size >= 5) {
                    DiceCard(player = newStickers[0], isRevealed = revealedIndex == 0, isEnabled = revealedIndex == -1, modifier = Modifier.align(Alignment.TopStart)) { revealedIndex = 0 }
                    DiceCard(player = newStickers[1], isRevealed = revealedIndex == 1, isEnabled = revealedIndex == -1, modifier = Modifier.align(Alignment.TopEnd)) { revealedIndex = 1 }
                    DiceCard(player = newStickers[2], isRevealed = revealedIndex == 2, isEnabled = revealedIndex == -1, modifier = Modifier.align(Alignment.Center)) { revealedIndex = 2 }
                    DiceCard(player = newStickers[3], isRevealed = revealedIndex == 3, isEnabled = revealedIndex == -1, modifier = Modifier.align(Alignment.BottomStart)) { revealedIndex = 3 }
                    DiceCard(player = newStickers[4], isRevealed = revealedIndex == 4, isEnabled = revealedIndex == -1, modifier = Modifier.align(Alignment.BottomEnd)) { revealedIndex = 4 }
                }
            }
            
            if (revealedIndex != -1) {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { packViewModel.clearNewStickers() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "COLLECT", color = Color.Black, fontWeight = FontWeight.Black)
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
    Surface(
        modifier = Modifier
            .size(240.dp, 330.dp)
            .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = WorldCupGold),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black,
        border = androidx.compose.foundation.BorderStroke(2.dp, Brush.linearGradient(listOf(WorldCupGold, Color.Black)))
    ) {
        Box(modifier = Modifier.background(
            Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color.Black))
        )) {
            // Metallic Shine Effect
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.05f),
                    0.5f to Color.Transparent,
                    1.0f to Color.White.copy(alpha = 0.05f)
                )
            ))
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "2026", color = WorldCupGold, fontSize = 64.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                Text(text = "OFFICIAL PACK", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                
                Spacer(modifier = Modifier.height(30.dp))
                
                Surface(
                    color = WorldCupYellow,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "5 STICKERS",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.Black,
                        fontSize = 12.sp,
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
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = modifier
            .size(105.dp, 145.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
                alpha = if (isEnabled || isRevealed) 1f else 0.3f
                scaleX = if (isRevealed) 1.1f else 1.0f
                scaleY = if (isRevealed) 1.1f else 1.0f
            }
            .clickable(enabled = isEnabled, onClick = onClick)
    ) {
        if (rotation <= 90f) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = WorldCupYellow,
                border = androidx.compose.foundation.BorderStroke(3.dp, Color.Black)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "FIFA", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
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