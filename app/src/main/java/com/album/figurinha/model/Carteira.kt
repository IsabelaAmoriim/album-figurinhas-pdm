package com.album.figurinha.model

data class Carteira(
    var moedas: Int = 0
) {

    fun adicionarMoedas(quantidade: Int) {
        if (quantidade > 0) {
            moedas += quantidade
        }
    }

    fun gastarMoedas(quantidade: Int): Boolean {
        if (quantidade <= 0 || moedas < quantidade) {
            return false
        }

        moedas -= quantidade
        return true
    }
}