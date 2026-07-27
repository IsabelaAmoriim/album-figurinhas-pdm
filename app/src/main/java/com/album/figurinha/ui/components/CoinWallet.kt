package com.album.figurinha.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.album.figurinha.ui.theme.WorldCupYellow

@Composable
fun CoinWallet(balance: Int, modifier: Modifier = Modifier) {
    // Pulse animation when balance changes
    var lastBalance by remember { mutableIntStateOf(balance) }
    val scale by animateFloatAsState(
        targetValue = if (balance != lastBalance) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { lastBalance = balance },
        label = "pulse"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .background(Color(0xFF1A243F), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Coins",
            tint = WorldCupYellow,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        AnimatedContent(
            targetState = balance,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                    slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                    slideOutVertically { height -> height } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "balance_counter"
        ) { targetBalance ->
            Text(
                text = "$targetBalance",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}