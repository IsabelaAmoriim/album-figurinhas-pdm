package com.album.figurinha

import com.album.figurinha.model.Carteira
import org.junit.Assert.*
import org.junit.Test

class CarteiraTest {

    @Test
    fun testAdicionarMoedas() {
        val carteira = Carteira(moedas = 100)
        carteira.adicionarMoedas(50)
        assertEquals(150, carteira.moedas)
    }

    @Test
    fun testAdicionarMoedasNegativasOuZeroNaoAltera() {
        val carteira = Carteira(moedas = 100)
        carteira.adicionarMoedas(-10)
        assertEquals(100, carteira.moedas)
        carteira.adicionarMoedas(0)
        assertEquals(100, carteira.moedas)
    }

    @Test
    fun testGastarMoedasComSucesso() {
        val carteira = Carteira(moedas = 100)
        val resultado = carteira.gastarMoedas(20)
        assertTrue(resultado)
        assertEquals(80, carteira.moedas)
    }

    @Test
    fun testGastarMoedasSaldoInsuficiente() {
        val carteira = Carteira(moedas = 10)
        val resultado = carteira.gastarMoedas(20)
        assertFalse(resultado)
        assertEquals(10, carteira.moedas)
    }

    @Test
    fun testGastarMoedasInvalido() {
        val carteira = Carteira(moedas = 100)
        val resultado = carteira.gastarMoedas(-5)
        assertFalse(resultado)
        assertEquals(100, carteira.moedas)
    }
}
