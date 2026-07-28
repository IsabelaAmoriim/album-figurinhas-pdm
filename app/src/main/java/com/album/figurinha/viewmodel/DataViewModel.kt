package com.album.figurinha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.album.figurinha.model.Coach
import com.album.figurinha.model.Player
import com.album.figurinha.model.PlayerDetails
import com.album.figurinha.model.Team
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.repository.FootballRepository.LoadResult
import com.album.figurinha.repository.StickerCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DataState(
    val isLoading: Boolean = true,
    val teams: List<Team> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel central responsavel por carregar todos os dados da Copa do Mundo
 * 2022 da API e popular o [StickerCatalog].
 *
 * Toda tela que precisa de dados de times/jogadores le desse ViewModel.
 * O catalogo e carregado primeiro para que AlbumViewModel possa contar com ele.
 */
class DataViewModel(
    private val repository: FootballRepository = FootballRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(DataState())
    val state: StateFlow<DataState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = DataState(isLoading = true)
            android.util.Log.d("DataViewModel", "loadData: iniciando carregamento...")

            try {
                val result = repository.loadAllWorldCupData()
                android.util.Log.d("DataViewModel", "loadData: ${result.teams.size} times, ${result.playerCount} jogadores")

                StickerCatalog.populateFrom(result)
                android.util.Log.d("DataViewModel", "loadData: catalogo populado com ${StickerCatalog.getTotalCount()} stickers")

                _state.value = DataState(
                    isLoading = false,
                    teams = result.teams
                )
            } catch (e: Exception) {
                android.util.Log.e("DataViewModel", "loadData: ERRO ${e.message}", e)
                _state.value = DataState(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar dados"
                )
            }
        }
    }

    fun getTeamById(teamId: Int): Team? =
        _state.value.teams.find { it.id == teamId }

    fun getPlayersForTeam(teamId: Int): List<Player> =
        _state.value.teams
            .find { it.id == teamId }
            ?.players ?: emptyList()

    fun getCoachForTeam(teamId: Int): Coach? =
        _state.value.teams
            .find { it.id == teamId }
            ?.coach

    fun getPlayerById(playerId: Int): Player? =
        _state.value.teams
            .flatMap { it.players }
            .find { it.id == playerId }

    fun getPlayerDetails(teamId: Int, playerId: Int, callback: (PlayerDetails?) -> Unit) {
        viewModelScope.launch {
            val details = repository.getPlayerDetails(teamId, playerId)
            callback(details)
        }
    }

    fun refresh() {
        StickerCatalog.reset()
        loadData()
    }
}
