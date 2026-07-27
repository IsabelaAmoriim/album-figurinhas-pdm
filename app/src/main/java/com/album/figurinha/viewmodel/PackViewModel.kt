package com.album.figurinha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Figurinha
import com.album.figurinha.model.PacoteFigurinha
import com.album.figurinha.model.Player
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.util.StickerImageResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PackViewModel(private val repository: FootballRepository = FootballRepository()) : ViewModel() {
    private val _newStickers = MutableStateFlow<List<Player>>(emptyList())
    val newStickers: StateFlow<List<Player>> = _newStickers.asStateFlow()

    private val _currentPack = MutableStateFlow<PacoteFigurinha?>(null)
    val currentPack: StateFlow<PacoteFigurinha?> = _currentPack.asStateFlow()

    private val _isOpening = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Fallback data with pre-resolved URLs (SoFifa)
    private val fallbackPlayers = listOf(
        Player(614, "Neymar Jr", "", 10, "ATACANTE", "...", 1),
        Player(732, "Vinícius Jr", "", 7, "ATACANTE", "...", 1),
        Player(154, "Lionel Messi", "", 10, "ATACANTE", "...", 2),
        Player(276, "K. Mbappé", "", 10, "ATACANTE", "...", 3),
        Player(874, "C. Ronaldo", "", 7, "ATACANTE", "...", 4)
    ).map { 
        it.copy(photo = StickerImageResolver.getPlayerImageUrl(it.id, "")) 
    }

    fun canAffordPack(walletViewModel: WalletViewModel): Boolean {
        return walletViewModel.wallet.value.moedas >= PACK_PRICE
    }

    fun openPack(walletViewModel: WalletViewModel): Boolean {
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
                            // Resolve URL immediately
                            players.add(p.copy(photo = StickerImageResolver.getPlayerImageUrl(p.id, p.photo)))
                        }
                    }
                    
                    if (players.size < 5) {
                         players.addAll(fallbackPlayers.take(5 - players.size))
                    }
                    
                    val finalPlayers = players.shuffled()

                    // 3. Pacote adicionado para abertura
                    val pacote = PacoteFigurinha(
                        id = (1..100000).random(),
                        openDate = LocalDateTime.now(),
                        stickers = finalPlayers.map { p ->
                            Figurinha(
                                id = p.id,
                                name = p.name,
                                image = p.photo,
                                rarity = 1,
                                playerId = p.id,
                                teamId = p.teamId
                            )
                        }
                    )
                    
                    _currentPack.value = pacote
                    _newStickers.value = finalPlayers
                } catch (e: Exception) {
                    println("PackViewModel: API Error: ${e.message}. Using fallback data.")
                    val finalPlayers = fallbackPlayers.shuffled()
                    val pacote = PacoteFigurinha(
                        id = (1..100000).random(),
                        openDate = LocalDateTime.now(),
                        stickers = finalPlayers.map { p ->
                            Figurinha(
                                id = p.id,
                                name = p.name,
                                image = p.photo,
                                rarity = 1,
                                playerId = p.id,
                                teamId = p.teamId
                            )
                        }
                    )
                    _currentPack.value = pacote
                    _newStickers.value = finalPlayers
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
