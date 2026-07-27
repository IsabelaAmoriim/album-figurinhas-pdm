package com.album.figurinha.repository

import com.album.figurinha.model.StickerRarity

/**
 * Regras de venda de figurinhas repetidas (RF27 / RF34 / RF36).
 *
 * Arquivo unico com todas as regras de preco e elegibilidade. Reutiliza
 * [StickerRarity], que ja existe no projeto -- nao existe um segundo enum de
 * raridade.
 *
 * TABELA DE PRECOS
 *  COMMON     ->  10
 *  COACHING   ->  25
 *  SPECIAL    ->  25
 *  LEGENDARY  ->  50
 *  MYTHIC     ->  50
 *
 * Sobre MYTHIC = 50 e nao 100: o sorteio de pacotes e uniforme sobre o
 * catalogo (ver PackViewModel / StickerCatalog.randomSticker). Nao existe
 * peso por raridade em lugar nenhum do codigo, entao uma figurinha MYTHIC tem
 * exatamente a mesma chance de drop de uma LEGENDARY. Como o preco deve ser
 * inversamente proporcional a chance de drop, os dois niveis ficam iguais.
 * Quando o sorteio ponderado for implementado, MYTHIC volta para 100.
 */
object StickerPricing {

    const val PRECO_COMMON = 10
    const val PRECO_COACHING = 25
    const val PRECO_SPECIAL = 25
    const val PRECO_LEGENDARY = 50

    /**
     * Igualado a LEGENDARY enquanto o sorteio for uniforme.
     * Valor pretendido quando houver sorteio ponderado: 100.
     */
    const val PRECO_MYTHIC = 50

    fun precoPorRaridade(
        rarity: StickerRarity
    ): Int = when (rarity) {
        StickerRarity.COMMON -> PRECO_COMMON
        StickerRarity.COACHING -> PRECO_COACHING
        StickerRarity.SPECIAL -> PRECO_SPECIAL
        StickerRarity.LEGENDARY -> PRECO_LEGENDARY
        StickerRarity.MYTHIC -> PRECO_MYTHIC
    }

    /** Preco unitario de uma figurinha do catalogo. */
    fun precoDaFigurinha(
        stickerId: Int
    ): Int =
        precoPorRaridade(
            StickerCatalog.getRarity(stickerId)
        )

    /**
     * Quantidade vendavel: tudo que excede a primeira unidade.
     * Garante que vender nunca re-bloqueia a figurinha nem reduz o progresso.
     */
    fun quantidadeVendavel(
        quantidadePossuida: Int
    ): Int =
        (quantidadePossuida - 1).coerceAtLeast(0)

    /** Uma figurinha so e vendavel se houver repetida. */
    fun isVendavel(
        quantidadePossuida: Int
    ): Boolean =
        quantidadePossuida > 1

    /** Valor total de N unidades de uma raridade. */
    fun valorTotal(
        rarity: StickerRarity,
        quantidade: Int
    ): Int =
        if (quantidade <= 0) {
            0
        } else {
            precoPorRaridade(rarity) * quantidade
        }
}
