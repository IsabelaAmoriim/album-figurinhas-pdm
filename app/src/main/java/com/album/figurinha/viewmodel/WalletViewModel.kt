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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WalletViewModel @JvmOverloads constructor(
    application: Application,
    private val prefs: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
) : AndroidViewModel(application) {

    private val _wallet = MutableStateFlow(loadCarteira())
    val wallet: StateFlow<Carteira> = _wallet.asStateFlow()

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun loadCarteira(): Carteira {
        val moedas = prefs.getInt(KEY_MOEDAS, DEFAULT_MOEDAS)
        val dataUltimoResgate = prefs.getString(KEY_DATA_ULTIMO_RESGATE, null)
        val hoje = getTodayDateString()

        // O resgate é permitido apenas se a data do último resgate for diferente de hoje
        val recompensasDisponiveis = dataUltimoResgate != hoje

        return Carteira(
            moedas = moedas,
            recompensasDisponiveis = recompensasDisponiveis,
            dataUltimoResgate = dataUltimoResgate
        )
    }

    private fun saveWallet(carteira: Carteira) {
        prefs.edit()
            .putInt(KEY_MOEDAS, carteira.moedas)
            .putBoolean(KEY_RECOMPENSAS, carteira.recompensasDisponiveis)
            .putString(KEY_DATA_ULTIMO_RESGATE, carteira.dataUltimoResgate)
            .apply()
    }

    fun claimDailyReward() {
        _wallet.update { current ->
            val hoje = getTodayDateString()
            if (current.dataUltimoResgate != hoje) {
                val updated = current.copy(
                    recompensasDisponiveis = false,
                    dataUltimoResgate = hoje
                )
                updated.adicionarMoedas(50)
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
        const val KEY_DATA_ULTIMO_RESGATE = "data_ultimo_resgate"
        const val DEFAULT_MOEDAS = 100
        const val DEFAULT_RECOMPENSAS = true
    }
}
