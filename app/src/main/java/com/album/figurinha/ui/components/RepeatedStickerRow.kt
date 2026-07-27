package com.album.figurinha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.repository.StickerPricing
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.RepeatedSticker

/**
 * Linha da aba "Repetidas": miniatura, nome, excedente disponível,
 * valor unitário, seletor de quantidade e botão de venda.
 */
@Composable
fun RepeatedStickerRow(
    item: RepeatedSticker,
    enabled: Boolean,
    onSell: (quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Reinicia o seletor quando o excedente muda.
    var quantity by remember(
        item.sticker.id,
        item.sellableQuantity
    ) {
        mutableIntStateOf(1)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = LightSheetCard
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            item.sticker.rarity.color.copy(
                                alpha = 0.15f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = item.sticker.imageUrl,
                        contentDescription = item.sticker.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.sticker.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextOnLightPrimary,
                        maxLines = 1
                    )

                    Text(
                        text = "${item.sellableQuantity} disponível(is) · ${item.unitPrice} moedas cada",
                        fontSize = 11.sp,
                        color = TextOnLightSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = item.sticker.rarity.color.copy(
                        alpha = 0.18f
                    )
                ) {
                    Text(
                        text = item.sticker.rarity.label,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = TextOnLightPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityStepper(
                    quantity = quantity,
                    min = 1,
                    max = item.sellableQuantity,
                    enabled = enabled,
                    onChange = {
                        quantity = it
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        onSell(quantity)
                    },
                    enabled = enabled && item.sellableQuantity > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WorldCupGold,
                        disabledContainerColor = LightSheetDivider
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        text = "VENDER ${StickerPricing.valorTotal(item.sticker.rarity, quantity)}",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/** Seletor "- N +" limitado ao excedente vendável. */
@Composable
fun QuantityStepper(
    quantity: Int,
    min: Int,
    max: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                LightSheetDivider.copy(alpha = 0.6f)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(
            enabled = enabled && quantity > min,
            onClick = {
                onChange(
                    (quantity - 1).coerceAtLeast(min)
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Diminuir quantidade",
                tint = TextOnLightPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = "$quantity",
            modifier = Modifier.widthIn(min = 28.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = TextOnLightPrimary
        )

        StepperButton(
            enabled = enabled && quantity < max,
            onClick = {
                onChange(
                    (quantity + 1).coerceAtMost(max)
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Aumentar quantidade",
                tint = TextOnLightPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun StepperButton(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.size(32.dp),
        shape = CircleShape,
        color = if (enabled) {
            LightSheetCard
        } else {
            Color.Transparent
        },
        enabled = enabled,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun RepeatedStickerRowPreview() {
    val sticker = CatalogSticker(
        id = 154,
        name = "Lionel Messi",
        category = StickerCategory.JOGADOR,
        rarity = StickerRarity.LEGENDARY,
        imageUrl = "",
        teamId = 2,
        number = 10,
        position = "ATACANTE",
        description = "Capitão da Seleção Argentina"
    )

    FigurinhaTheme {
        RepeatedStickerRow(
            item = RepeatedSticker(
                sticker = sticker,
                quantity = 4,
                sellableQuantity = 3,
                unitPrice = 50
            ),
            enabled = true,
            onSell = {}
        )
    }
}
