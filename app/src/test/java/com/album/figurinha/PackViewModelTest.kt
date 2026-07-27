package com.album.figurinha

import android.app.Application
import com.album.figurinha.model.TeamResponse
import com.album.figurinha.repository.FootballRepository
import com.album.figurinha.viewmodel.PackViewModel
import com.album.figurinha.viewmodel.WalletViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PackViewModelTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var fakeApplication: Application
    private lateinit var walletViewModel: WalletViewModel
    private lateinit var packViewModel: PackViewModel

    @Before
    fun setUp() {
        fakePrefs = FakeSharedPreferences()
        fakeApplication = Application()
        walletViewModel = WalletViewModel(fakeApplication, fakePrefs)
        packViewModel = PackViewModel(FakeFootballRepository())
    }

    @Test
    fun testVerificacaoDeSaldoComSaldoSuficiente() {
        assertTrue(packViewModel.canAffordPack(walletViewModel))
    }

    @Test
    fun testVerificacaoDeSaldoComSaldoInsuficiente() {
        // Gasta 90 moedas para restar apenas 10
        walletViewModel.spendCoins(90)
        assertFalse(packViewModel.canAffordPack(walletViewModel))
    }

    @Test
    fun testOpenPackComSaldoInsuficienteFalhaENaoDescontaMoedas() {
        walletViewModel.spendCoins(90) // Saldo restante: 10 moedas

        val success = packViewModel.openPack(walletViewModel)

        assertFalse(success)
        assertEquals(10, walletViewModel.wallet.value.moedas)
        assertNotNull(packViewModel.errorMessage.value)
    }

    @Test
    fun testOpenPackComSaldoSuficienteDescontaMoedasECriaPacote() {
        // Saldo inicial: 100 moedas
        val success = packViewModel.openPack(walletViewModel)

        assertTrue(success)
        // 20 moedas devem ser descontadas (saldo = 80)
        assertEquals(80, walletViewModel.wallet.value.moedas)
        assertNull(packViewModel.errorMessage.value)
    }
}

class FakeFootballRepository : FootballRepository() {
    override suspend fun getTeams(): TeamResponse {
        throw RuntimeException("API Offline for Test - Triggering Fallback")
    }
}
