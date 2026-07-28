package com.album.figurinha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.AlbumProgress

/**
 * Card de progresso do álbum.
 */
@Composable
fun AlbumProgressCard(
    progress: AlbumProgress,
    modifier: Modifier = Modifier,
    accentColor: Color = WorldCupGold
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = accentColor.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Figurinhas Coletadas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextOnLightPrimary
                )

                Text(
                    text = "Copa do Mundo 2026",
                    fontSize = 11.sp,
                    color = TextOnLightSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${progress.collected}/${progress.total}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = TextOnLightPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress.fraction },
                    modifier = Modifier
                        .width(110.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accentColor,
                    trackColor = LightSheetDivider
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${progress.percentage}% completo",
                    fontSize = 10.sp,
                    color = TextOnLightSecondary
                )
            }
        }
    }
}

/**
 * Divisor com rótulo central em caixa alta.
 */
@Composable
fun SectionDivider(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = TextOnLightSecondary
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = LightSheetDivider
        )

        Text(
            text = label.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = LightSheetDivider
        )
    }
}

/**
 * Par de pílulas usado como abas.
 */
@Composable
fun AlbumTabPills(
    selectedIndex: Int,
    labels: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                LightSheetDivider.copy(alpha = 0.5f)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = if (selected) {
                    WorldCupGold
                } else {
                    Color.Transparent
                },
                onClick = {
                    onSelect(index)
                }
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(
                        vertical = 10.dp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (selected) {
                        Color.Black
                    } else {
                        TextOnLightSecondary
                    }
                )
            }
        }
    }
}

/** Estado vazio da aba de repetidas. */
@Composable
fun AlbumEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = LightSheetCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LightSheetDivider),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = TextOnLightSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextOnLightPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                fontSize = 12.sp,
                color = TextOnLightSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun AlbumProgressCardPreview() {
    FigurinhaTheme {
        AlbumProgressCard(
            progress = AlbumProgress(
                collected = 6,
                total = 10
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun AlbumTabPillsPreview() {
    FigurinhaTheme {
        AlbumTabPills(
            selectedIndex = 0,
            labels = listOf(
                "FIGURINHAS",
                "REPETIDAS (2)"
            ),
            onSelect = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun AlbumEmptyStatePreview() {
    FigurinhaTheme {
        AlbumEmptyState(
            title = "Nenhuma figurinha repetida",
            message = "Abra pacotinhos na loja para conseguir repetidas e trocá-las por moedas."
        )
    }
}