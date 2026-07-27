package com.album.figurinha.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Player
import com.album.figurinha.repository.FootballRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackViewModel(private val repository: FootballRepository = FootballRepository()) : ViewModel() {
    private val _newStickers = MutableStateFlow<List<Player>>(emptyList())
    val newStickers: StateFlow<List<Player>> = _newStickers.asStateFlow()

    private val _isOpening = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

    // Fallback data in case API fails
    private val fallbackPlayers = listOf(
        Player(1, "Neymar Jr", "https://media.api-sports.io/football/players/614.png", 10, "ATACANTE", "...", 1),
        Player(2, "Vinícius Jr", "https://media.api-sports.io/football/players/732.png", 7, "ATACANTE", "...", 1),
        Player(3, "Lionel Messi", "https://media.api-sports.io/football/players/154.png", 10, "ATACANTE", "...", 2),
        Player(4, "C. Ronaldo", "https://media.api-sports.io/football/players/874.png", 7, "ATACANTE", "...", 4),
        Player(5, "K. Mbappé", "https://media.api-sports.io/football/players/276.png", 10, "ATACANTE", "...", 3)
    )

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
                            players.add(teamPlayers.random())
                        }
                    }
                    
                    if (players.size < 5) {
                         Log.w("PackViewModel", "API returned insufficient players, using some fallback")
                         players.addAll(fallbackPlayers.take(5 - players.size))
                    }
                    
                    _newStickers.value = players
                    Log.d("PackViewModel", "Pack opened successfully with ${players.size} players")
                } catch (e: Exception) {
                    Log.e("PackViewModel", "API Error: ${e.message}. Using fallback data.", e)
                    // If API fails, we MUST provide data so the UI animates
                    _newStickers.value = fallbackPlayers.shuffled().take(5)
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