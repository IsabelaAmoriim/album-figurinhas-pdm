package com.album.figurinha.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Player
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.util.StickerImageResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackViewModel(private val repository: FootballRepository = FootballRepository()) : ViewModel() {
    private val _newStickers = MutableStateFlow<List<Player>>(emptyList())
    val newStickers: StateFlow<List<Player>> = _newStickers.asStateFlow()

    private val _isOpening = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

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

    fun openPack(walletViewModel: WalletViewModel) {
        if (walletViewModel.spendCoins(20)) {
            _isOpening.value = true
            viewModelScope.launch {
                try {
                    Log.d("PackViewModel", "Attempting to fetch teams from API...")
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
                    
                    _newStickers.value = players.shuffled()
                } catch (e: Exception) {
                    Log.e("PackViewModel", "API Error: ${e.message}. Using fallback data.", e)
                    _newStickers.value = fallbackPlayers.shuffled()
                } finally {
                    _isOpening.value = false
                }
            }
        }
    }
    
    fun clearNewStickers() {
        _newStickers.value = emptyList()
    }
}
