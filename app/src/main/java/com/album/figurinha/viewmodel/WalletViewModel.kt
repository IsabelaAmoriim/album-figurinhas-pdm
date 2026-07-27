package com.album.figurinha.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.album.figurinha.model.Carteira
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WalletViewModel @JvmOverloads constructor(
    application: Application,
    private val prefs: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
) : AndroidViewModel(application) {

    private val _wallet = MutableStateFlow(loadCarteira())
    val wallet: StateFlow<Carteira> = _wallet.asStateFlow()

    private fun loadCarteira(): Carteira {
        val moedas = prefs.getInt(KEY_MOEDAS, DEFAULT_MOEDAS)
        val recompensasDisponiveis = prefs.getBoolean(KEY_RECOMPENSAS, DEFAULT_RECOMPENSAS)
        return Carteira(moedas = moedas, recompensasDisponiveis = recompensasDisponiveis)
    }

    private fun saveWallet(carteira: Carteira) {
        prefs.edit()
            .putInt(KEY_MOEDAS, carteira.moedas)
            .putBoolean(KEY_RECOMPENSAS, carteira.recompensasDisponiveis)
            .apply()
    }

    fun claimDailyReward() {
        _wallet.update { current ->
            if (current.recompensasDisponiveis) {
                val updated = current.copy()
                updated.adicionarMoedas(50)
                updated.recompensasDisponiveis = false
                saveWallet(updated)
                updated
            } else {
                current
            }
        }
    }

    fun addCoins(amount: Int) {
        if (amount <= 0) return
        _wallet.update { current ->
            val updated = current.copy()
            updated.adicionarMoedas(amount)
            saveWallet(updated)
            updated
        }
    }

    fun spendCoins(amount: Int): Boolean {
        var success = false
        _wallet.update { current ->
            val updated = current.copy()
            if (updated.gastarMoedas(amount)) {
                success = true
                saveWallet(updated)
                updated
            } else {
                success = false
                current
            }
        }
        return success
    }

    companion object {
        const val PREFS_NAME = "wallet_prefs"
        const val KEY_MOEDAS = "moedas"
        const val KEY_RECOMPENSAS = "recompensas_disponiveis"
        const val DEFAULT_MOEDAS = 100
        const val DEFAULT_RECOMPENSAS = true
    }
}
