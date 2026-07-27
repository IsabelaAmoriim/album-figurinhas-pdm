package com.album.figurinha.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.album.figurinha.model.Carteira
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel : ViewModel() {
    private val _wallet = MutableStateFlow(Carteira(moedas = 100))
    val wallet: StateFlow<Carteira> = _wallet.asStateFlow()

    fun claimDailyReward() {
        if (_wallet.value.recompensasDisponiveis) {
            val current = _wallet.value
            current.adicionarMoedas(50) // Daily reward amount
            current.recompensasDisponiveis = false
            _wallet.value = current.copy()
        }
    }

    fun addCoins(amount: Int) {
        val current = _wallet.value
        current.adicionarMoedas(amount)
        _wallet.value = current.copy()
    }

    fun spendCoins(amount: Int): Boolean {
        val current = _wallet.value
        val success = current.gastarMoedas(amount)
        if (success) {
            _wallet.value = current.copy()
        }
        return success
    }
}