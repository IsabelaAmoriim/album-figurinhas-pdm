package com.album.figurinha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.AlbumSlot

/**
 * Detalhe da figurinha obtida (RF29): nome, seleção, posição, número,
 * raridade e descrição. Slots bloqueados nunca chegam aqui.
 */
@Composable
fun StickerDetailDialog(
    slot: AlbumSlot,
    teamName: String,
    onDismiss: () -> Unit
) {
    val sticker = slot.sticker
    val accent = sticker.rarity.color

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = LightSheetCard
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            accent.copy(alpha = 0.18f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = sticker.imageUrl,
                        contentDescription = sticker.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(120.dp)
                    )

                    if (slot.quantity > 1) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = WorldCupGold
                        ) {
                            Text(
                                text = "x${slot.quantity}",
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 3.dp
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = sticker.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextOnLightPrimary
                    )

                    Text(
                        text = sticker.position.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = accent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow(
                        label = "Seleção",
                        value = teamName
                    )

                    if (sticker.category == StickerCategory.JOGADOR) {
                        DetailRow(
                            label = "Número",
                            value = "${sticker.number}"
                        )
                    }

                    DetailRow(
                        label = "Raridade",
                        value = sticker.rarity.label
                    )

                    DetailRow(
                        label = "Quantidade",
                        value = "${slot.quantity}"
                    )

                    if (sticker.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(accent)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "SOBRE A FIGURINHA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextOnLightPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = sticker.description,
                            fontSize = 13.sp,
                            color = TextOnLightSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent
                        )
                    ) {
                        Text(
                            text = "FECHAR",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextOnLightSecondary
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextOnLightPrimary
        )
    }
}

/**
 * Confirmação de venda (RF34): diz quantas figurinhas e quantas moedas.
 * [stickerName] nulo representa a venda de todas as repetidas.
 */
@Composable
fun SellConfirmationDialog(
    stickerName: String?,
    quantity: Int,
    totalCoins: Int,
    enabled: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val alvo = stickerName?.let {
        "$quantity figurinha(s) de $it"
    } ?: "$quantity figurinha(s) repetida(s)"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LightSheetCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Confirmar venda",
                fontWeight = FontWeight.Black,
                color = TextOnLightPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Você vai vender $alvo e receber $totalCoins moedas.",
                    fontSize = 14.sp,
                    color = TextOnLightSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Uma unidade de cada figurinha sempre permanece no álbum.",
                    fontSize = 12.sp,
                    color = TextOnLightSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = enabled,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WorldCupGold,
                    disabledContainerColor = LightSheetDivider
                )
            ) {
                Text(
                    text = "VENDER",
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "CANCELAR",
                    fontWeight = FontWeight.Bold,
                    color = TextOnLightSecondary
                )
            }
        }
    )
}

@Preview
@Composable
private fun StickerDetailDialogPreview() {
    val sticker = CatalogSticker(
        id = 614,
        name = "Neymar Jr",
        category = StickerCategory.JOGADOR,
        rarity = StickerRarity.SPECIAL,
        imageUrl = "",
        teamId = 1,
        number = 10,
        position = "ATACANTE",
        description = "Astral do Brasil"
    )

    FigurinhaTheme {
        StickerDetailDialog(
            slot = AlbumSlot(
                sticker = sticker,
                quantity = 3
            ),
            teamName = "Brasil",
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun SellConfirmationDialogPreview() {
    FigurinhaTheme {
        SellConfirmationDialog(
            stickerName = "Lionel Messi",
            quantity = 2,
            totalCoins = 100,
            enabled = true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
