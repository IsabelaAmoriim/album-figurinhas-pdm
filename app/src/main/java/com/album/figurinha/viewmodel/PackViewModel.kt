package com.album.figurinha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Figurinha
import com.album.figurinha.model.PacoteFigurinha
import com.album.figurinha.model.Player
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.repository.StickerCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class DrawnSticker(
    val player: Player,
    val rarity: com.album.figurinha.model.StickerRarity
)

class PackViewModel(
    private val repository: FootballRepository = FootballRepository()
) : ViewModel() {
    private val _newStickers = MutableStateFlow<List<DrawnSticker>>(emptyList())
    val newStickers: StateFlow<List<DrawnSticker>> = _newStickers.asStateFlow()

    private val _currentPack = MutableStateFlow<PacoteFigurinha?>(null)
    val currentPack: StateFlow<PacoteFigurinha?> = _currentPack.asStateFlow()

    private val _isOpening = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun canAffordPack(walletViewModel: WalletViewModel): Boolean {
        return walletViewModel.wallet.value.moedas >= PACK_PRICE
    }

    fun openPack(walletViewModel: WalletViewModel, albumViewModel: AlbumViewModel? = null): Boolean {
        if (!canAffordPack(walletViewModel)) {
            _errorMessage.value = "Saldo insuficiente para comprar pacote! Requer $PACK_PRICE moedas."
            return false
        }

        _errorMessage.value = null

        if (walletViewModel.spendCoins(PACK_PRICE)) {
            _isOpening.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val drawn = (1..5).map {
                        val catalogSticker = StickerCatalog.randomSticker()
                        val player = catalogSticker.toPlayer()
                        DrawnSticker(player, player.rarity)
                    }

                    val pacote = PacoteFigurinha(
                        id = (1..100000).random(),
                        openDate = LocalDateTime.now(),
                        stickers = drawn.map { d ->
                            Figurinha(
                                id = d.player.id,
                                name = d.player.name,
                                image = d.player.photo,
                                rarity = d.rarity.ordinal,
                                playerId = d.player.id,
                                teamId = d.player.teamId
                            )
                        }
                    )

                    _currentPack.value = pacote
                    _newStickers.value = drawn

                    albumViewModel?.addStickers(drawn.map { Pair(it.player.id, it.rarity) })

                } catch (e: Exception) {
                    // Fallback: se o catalogo ainda nao carregou, usa os stickers disponiveis
                    val fallback = StickerCatalog.allStickers
                    if (fallback.isNotEmpty()) {
                        val drawn = (1..5).map {
                            val s = fallback.random()
                            DrawnSticker(s.toPlayer(), s.rarity)
                        }
                        _currentPack.value = PacoteFigurinha(
                            id = (1..100000).random(),
                            openDate = LocalDateTime.now(),
                            stickers = drawn.map { Figurinha(it.player.id, it.player.name, it.player.photo, it.rarity.ordinal, it.player.id, it.player.teamId) }
                        )
                        _newStickers.value = drawn
                        albumViewModel?.addStickers(drawn.map { Pair(it.player.id, it.rarity) })
                    } else {
                        _errorMessage.value = "Erro ao abrir pacote: ${e.message}"
                    }
                } finally {
                    _isOpening.value = false
                }
            }
            return true
        } else {
            _errorMessage.value = "Saldo insuficiente para realizar a compra."
            return false
        }
    }

    fun clearNewStickers() {
        _newStickers.value = emptyList()
        _currentPack.value = null
        _errorMessage.value = null
    }

    companion object {
        const val PACK_PRICE = 20
    }
}
