package com.album.figurinha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Figurinha
import com.album.figurinha.model.PacoteFigurinha
import com.album.figurinha.model.Player
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.repository.PlayersData
import com.album.figurinha.util.StickerImageResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class DrawnSticker(
    val player: Player,
    val rarity: StickerRarity
)

class PackViewModel(private val repository: FootballRepository = FootballRepository()) : ViewModel() {
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
        // 1. Verificação de saldo
        if (!canAffordPack(walletViewModel)) {
            _errorMessage.value = "Saldo insuficiente para comprar pacote! Requer $PACK_PRICE moedas."
            return false
        }

        _errorMessage.value = null

        // 2. Desconto das moedas
        if (walletViewModel.spendCoins(PACK_PRICE)) {
            _isOpening.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    println("PackViewModel: Attempting to fetch teams from API...")
                    val response = repository.getTeams()
                    val teams = response.response.map { it.team }
                    
                    val players = mutableListOf<Player>()
                    repeat(5) {
                        val randomTeam = teams.random()
                        val teamPlayers = repository.getPlayers(randomTeam.id).response.map { it.player }
                        if (teamPlayers.isNotEmpty()) {
                            val p = teamPlayers.random()
                            val rarity = PlayersData.getRarityForPlayer(p.id)
                            players.add(p.copy(
                                photo = StickerImageResolver.getPlayerImageUrl(p.id, p.photo),
                                rarity = rarity
                            ))
                        }
                    }
                    
                    if (players.size < 5) {
                        val fallback = PlayersData.allPlayers
                        players.addAll(fallback.take(5 - players.size))
                    }
                    
                    val drawn = players.shuffled().map { p ->
                        val rarity = PlayersData.getRarityForPlayer(p.id)
                        val updatedPlayer = p.copy(rarity = rarity)
                        DrawnSticker(updatedPlayer, rarity)
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

                    // Adiciona automaticamente as figurinhas sorteadas ao álbum do usuário
                    albumViewModel?.addStickers(drawn.map { Pair(it.player.id, it.rarity) })

                } catch (e: Exception) {
                    println("PackViewModel: API Error: ${e.message}. Using fallback data.")
                    val fallback = PlayersData.allPlayers
                    val drawn = fallback.shuffled().map { p ->
                        DrawnSticker(p, p.rarity)
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

                    // Adiciona automaticamente as figurinhas sorteadas ao álbum do usuário
                    albumViewModel?.addStickers(drawn.map { Pair(it.player.id, it.rarity) })
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
