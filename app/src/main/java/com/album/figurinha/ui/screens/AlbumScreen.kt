package com.album.figurinha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.ui.components.*
import com.album.figurinha.ui.theme.*
import com.album.figurinha.viewmodel.AlbumSection
import com.album.figurinha.viewmodel.AlbumSlot
import com.album.figurinha.viewmodel.AlbumViewModel
import com.album.figurinha.viewmodel.WalletViewModel

/** Venda aguardando confirmação. [sticker] nulo = vender todas as repetidas. */
private data class PendingSale(
    val sticker: CatalogSticker?,
    val quantity: Int,
    val totalCoins: Int
)

/**
 * Tela do álbum (issue #8) com a aba de repetidas (issue #13).
 */
@Composable
fun AlbumScreen(
    albumViewModel: AlbumViewModel,
    walletViewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val progress by albumViewModel.progress.collectAsState()
    val sections by albumViewModel.sections.collectAsState()
    val repeated by albumViewModel.repeatedStickers.collectAsState()
    val isSelling by albumViewModel.isSelling.collectAsState()
    val errorMessage by albumViewModel.errorMessage.collectAsState()
    val sellMessage by albumViewModel.sellMessage.collectAsState()
    val walletState by walletViewModel.wallet.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    var detailSlot by remember {
        mutableStateOf<AlbumSlot?>(null)
    }

    var pendingSale by remember {
        mutableStateOf<PendingSale?>(null)
    }

    // RF21 - falha
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            albumViewModel.clearMessages()
        }
    }

    // Sucesso da venda
    LaunchedEffect(sellMessage) {
        sellMessage?.let {
            snackbarHostState.showSnackbar(it)
            albumViewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = LightSheet
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightSheet)
                .verticalScroll(rememberScrollState())
        ) {
            AlbumHeader(
                balance = walletState.moedas,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 32.dp,
                            topEnd = 32.dp
                        )
                    )
                    .background(LightSheet)
                    .padding(16.dp)
            ) {
                AlbumProgressCard(
                    progress = progress
                )

                Spacer(modifier = Modifier.height(16.dp))

                AlbumTabPills(
                    selectedIndex = selectedTab,
                    labels = listOf(
                        "FIGURINHAS",
                        if (repeated.isEmpty()) {
                            "REPETIDAS"
                        } else {
                            "REPETIDAS (${repeated.size})"
                        }
                    ),
                    onSelect = {
                        selectedTab = it
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 0) {
                    AlbumGrid(
                        sections = sections,
                        onSlotClick = { slot ->
                            // Slot bloqueado não abre detalhe.
                            if (slot.unlocked) {
                                detailSlot = slot
                            }
                        }
                    )
                } else {
                    RepeatedTab(
                        repeated = repeated,
                        totalValue = repeated.sumOf {
                            it.totalValue
                        },
                        isSelling = isSelling,
                        onSellOne = { item, quantity ->
                            pendingSale = PendingSale(
                                sticker = item.sticker,
                                quantity = quantity,
                                totalCoins = item.unitPrice * quantity
                            )
                        },
                        onSellAll = { totalCards, totalCoins ->
                            pendingSale = PendingSale(
                                sticker = null,
                                quantity = totalCards,
                                totalCoins = totalCoins
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    detailSlot?.let { slot ->
        StickerDetailDialog(
            slot = slot,
            teamName = teamDisplayName(
                slot.sticker.teamId
            ),
            onDismiss = {
                detailSlot = null
            }
        )
    }

    pendingSale?.let { sale ->
        SellConfirmationDialog(
            stickerName = sale.sticker?.name,
            quantity = sale.quantity,
            totalCoins = sale.totalCoins,
            enabled = !isSelling,
            onConfirm = {
                if (sale.sticker == null) {
                    albumViewModel.sellAllRepeated(
                        walletViewModel
                    )
                } else {
                    albumViewModel.sellStickers(
                        sale.sticker.id,
                        sale.quantity,
                        walletViewModel
                    )
                }

                pendingSale = null
            },
            onDismiss = {
                pendingSale = null
            }
        )
    }
}

/** Cabeçalho escuro do álbum, na identidade da Home. */
@Composable
private fun AlbumHeader(
    balance: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        DarkBlueBg,
                        CardBackground
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(
                    Color.Black.copy(alpha = 0.25f),
                    RoundedCornerShape(8.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            CoinWallet(
                balance = balance
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(WorldCupGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MEU ÁLBUM",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Copa do Mundo 2026",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

/** Grid agrupado por categoria. Duas colunas, como o elenco das seleções. */
@Composable
private fun AlbumGrid(
    sections: List<AlbumSection>,
    onSlotClick: (AlbumSlot) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        sections.forEach { section ->
            Column {
                SectionDivider(
                    label = "${section.category.label} ${section.collected}/${section.total}"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    section.slots
                        .chunked(2)
                        .forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    StickerCard(
                                        player = slot.sticker.toPlayer(),
                                        isCollected = slot.unlocked,
                                        teamColor = teamColorFor(
                                            slot.sticker.teamId
                                        ),
                                        teamShield = null,
                                        rarity = slot.sticker.rarity,
                                        quantity = slot.quantity,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable(
                                                enabled = slot.unlocked
                                            ) {
                                                onSlotClick(slot)
                                            }
                                    )
                                }

                                if (rowSlots.size == 1) {
                                    Spacer(
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

/** Aba "Repetidas": vender todas no topo, depois uma linha por figurinha. */
@Composable
private fun RepeatedTab(
    repeated: List<com.album.figurinha.viewmodel.RepeatedSticker>,
    totalValue: Int,
    isSelling: Boolean,
    onSellOne: (
        com.album.figurinha.viewmodel.RepeatedSticker,
        Int
    ) -> Unit,
    onSellAll: (
        totalCards: Int,
        totalCoins: Int
    ) -> Unit
) {
    if (repeated.isEmpty()) {
        AlbumEmptyState(
            title = "Nenhuma figurinha repetida",
            message = "Abra pacotinhos na loja para conseguir repetidas e trocá-las por moedas."
        )

        return
    }

    val totalCards = repeated.sumOf {
        it.sellableQuantity
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = WorldCupGold.copy(alpha = 0.14f)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$totalCards repetida(s) disponível(is)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextOnLightPrimary
                    )

                    Text(
                        text = "Valem $totalValue moedas no total",
                        fontSize = 11.sp,
                        color = TextOnLightSecondary
                    )
                }

                Button(
                    onClick = {
                        onSellAll(
                            totalCards,
                            totalValue
                        )
                    },
                    enabled = !isSelling,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WorldCupGold,
                        disabledContainerColor = LightSheetDivider
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        text = "VENDER TODAS",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        repeated.forEach { item ->
            RepeatedStickerRow(
                item = item,
                enabled = !isSelling,
                onSell = { quantity ->
                    onSellOne(
                        item,
                        quantity
                    )
                }
            )
        }
    }
}

private fun teamColorFor(
    teamId: Int
): Color = when (teamId) {
    6 -> BrazilGreen
    8 -> ArgentinaBlue
    9 -> SpainRed
    else -> WorldCupGold
}

private fun teamDisplayName(
    teamId: Int
): String = when (teamId) {
    6 -> "Brasil"
    8 -> "Argentina"
    9 -> "Espanha"
    else -> "—"
}
