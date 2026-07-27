package com.album.figurinha

import android.app.Application
import android.content.SharedPreferences
import com.album.figurinha.viewmodel.WalletViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WalletViewModelTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var fakeApplication: Application

    @Before
    fun setUp() {
        fakePrefs = FakeSharedPreferences()
        // Dummy Application instance for constructor signature
        fakeApplication = Application()
    }

    @Test
    fun testEstadoInicialSaldoExibido() {
        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        val state = viewModel.wallet.value
        assertEquals(100, state.moedas)
        assertTrue(state.recompensasDisponiveis)
    }

    @Test
    fun testClaimDailyRewardAtualizaESalvaSaldo() {
        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        viewModel.claimDailyReward()

        val state = viewModel.wallet.value
        assertEquals(150, state.moedas)
        assertFalse(state.recompensasDisponiveis)

        // Verifica a persistência no SharedPreferences
        assertEquals(150, fakePrefs.getInt(WalletViewModel.KEY_MOEDAS, 0))
        assertFalse(fakePrefs.getBoolean(WalletViewModel.KEY_RECOMPENSAS, true))
    }

    @Test
    fun testSpendCoinsAtualizaESalvaSaldo() {
        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        val success = viewModel.spendCoins(20)

        assertTrue(success)
        assertEquals(80, viewModel.wallet.value.moedas)
        assertEquals(80, fakePrefs.getInt(WalletViewModel.KEY_MOEDAS, 0))
    }

    @Test
    fun testAddCoinsAtualizaESalvaSaldo() {
        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        viewModel.addCoins(50)

        assertEquals(150, viewModel.wallet.value.moedas)
        assertEquals(150, fakePrefs.getInt(WalletViewModel.KEY_MOEDAS, 0))
    }

    @Test
    fun testApenasUmResgatePorDia() {
        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        viewModel.claimDailyReward()

        // Primeiro resgate: saldo passa de 100 para 150
        assertEquals(150, viewModel.wallet.value.moedas)
        assertFalse(viewModel.wallet.value.recompensasDisponiveis)
        assertNotNull(viewModel.wallet.value.dataUltimoResgate)

        // Tentativa de segundo resgate no mesmo dia não deve alterar o saldo
        viewModel.claimDailyReward()
        assertEquals(150, viewModel.wallet.value.moedas)
    }

    @Test
    fun testResgateLiberadoEmNovoDia() {
        // Simula último resgate realizado ontem ("2026-07-26")
        fakePrefs.edit().putString(WalletViewModel.KEY_DATA_ULTIMO_RESGATE, "2026-07-26").apply()

        val viewModel = WalletViewModel(fakeApplication, fakePrefs)
        // Como o resgate foi ontem, a recompensa deve estar disponível hoje
        assertTrue(viewModel.wallet.value.recompensasDisponiveis)

        // Resgata o bônus hoje
        viewModel.claimDailyReward()
        assertEquals(150, viewModel.wallet.value.moedas)
        assertFalse(viewModel.wallet.value.recompensasDisponiveis)
    }

    @Test
    fun testPersistenciaEntreInstanciasDoViewModel() {
        // Primeira sessão: gasta 40 moedas e resgata recompensa diária (+50 moedas) -> 110 moedas
        val vm1 = WalletViewModel(fakeApplication, fakePrefs)
        vm1.spendCoins(40)
        vm1.claimDailyReward()
        assertEquals(110, vm1.wallet.value.moedas)

        // Segunda sessão: recria ViewModel com os mesmos SharedPreferences persistidos
        val vm2 = WalletViewModel(fakeApplication, fakePrefs)
        assertEquals(110, vm2.wallet.value.moedas)
        assertFalse(vm2.wallet.value.recompensasDisponiveis)
    }
}

class FakeSharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()

    override fun getInt(key: String?, defValue: Int): Int = (values[key] as? Int) ?: defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = (values[key] as? Boolean) ?: defValue
    override fun getString(key: String?, defValue: String?): String? = (values[key] as? String) ?: defValue
    override fun getLong(key: String?, defValue: Long): Long = (values[key] as? Long) ?: defValue
    override fun getFloat(key: String?, defValue: Float): Float = (values[key] as? Float) ?: defValue
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = (values[key] as? MutableSet<String>) ?: defValues
    override fun contains(key: String?): Boolean = values.containsKey(key)
    override fun getAll(): MutableMap<String, *> = values
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    override fun edit(): SharedPreferences.Editor = FakeEditor(values)

    class FakeEditor(private val map: MutableMap<String, Any>) : SharedPreferences.Editor {
        private val tempMap = mutableMapOf<String, Any>()

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            if (value != null) tempMap[key] = value else tempMap.remove(key)
            return this
        }
        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor {
            if (values != null) tempMap[key] = values else tempMap.remove(key)
            return this
        }
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = this
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = this
        override fun remove(key: String?): SharedPreferences.Editor = this
        override fun clear(): SharedPreferences.Editor = this
        override fun apply() {
            map.putAll(tempMap)
        }
        override fun commit(): Boolean {
            map.putAll(tempMap)
            return true
        }
    }
}
