package com.album.figurinha.repository

import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.util.StickerImageResolver

/**
 * Catalogo unico de figurinhas do album.
 *
 * Passa a ser a unica fonte de verdade de:
 *  - total de figurinhas do album (base do progresso);
 *  - raridade de cada figurinha;
 *  - imagem de cada figurinha.
 *
 * E montado a partir de [PlayersData] (jogadores) + das selecoes que antes
 * estavam hardcoded na HomeScreen.
 *
 * IDs das selecoes sao deslocados por [SELECTION_ID_OFFSET] para que nunca
 * colidam com ids de jogadores vindos da API-Sports (que sao inteiros baixos
 * e poderiam bater com os teamIds 1..4).
 */
object StickerCatalog {

    /** Deslocamento aplicado ao teamId para gerar o id da figurinha de selecao. */
    const val SELECTION_ID_OFFSET = 10_000

    /** id da figurinha de selecao correspondente a um teamId. */
    fun selectionStickerId(teamId: Int): Int =
        SELECTION_ID_OFFSET + teamId

    /** teamId correspondente a uma figurinha de selecao. */
    fun teamIdFromSelectionSticker(stickerId: Int): Int =
        stickerId - SELECTION_ID_OFFSET

    private val selecoes: List<CatalogSticker> = listOf(
        SelecaoSeed(
            1,
            "Brasil",
            "Pentacampeã mundial"
        ),
        SelecaoSeed(
            2,
            "Argentina",
            "Atual campeã mundial"
        ),
        SelecaoSeed(
            3,
            "França",
            "Bicampeã mundial"
        ),
        SelecaoSeed(
            4,
            "Portugal",
            "Campeã europeia em 2016"
        )
    ).map { seed ->
        CatalogSticker(
            id = selectionStickerId(seed.teamId),
            name = seed.name,
            category = StickerCategory.SELECAO,
            rarity = StickerRarity.COMMON,
            imageUrl = StickerImageResolver.getTeamShieldUrl(
                seed.teamId,
                ""
            ),
            teamId = seed.teamId,
            number = 0,
            position = "SELEÇÃO",
            description = seed.description
        )
    }

    private val jogadores: List<CatalogSticker> =
        PlayersData.allPlayers.map { player ->
            CatalogSticker(
                id = player.id,
                name = player.name,
                category = StickerCategory.JOGADOR,
                rarity = player.rarity,
                imageUrl = player.photo,
                teamId = player.teamId,
                number = player.number,
                position = player.position,
                description = player.description
            )
        }

    /** Todas as figurinhas do album, selecoes primeiro. */
    val allStickers: List<CatalogSticker> =
        selecoes + jogadores

    private val byId: Map<Int, CatalogSticker> =
        allStickers.associateBy { it.id }

    fun getById(stickerId: Int): CatalogSticker? =
        byId[stickerId]

    fun contains(stickerId: Int): Boolean =
        byId.containsKey(stickerId)

    /** Total de figurinhas do album. Denominador de todo calculo de progresso. */
    fun getTotalCount(): Int =
        allStickers.size

    fun getByCategory(
        category: StickerCategory
    ): List<CatalogSticker> =
        allStickers.filter {
            it.category == category
        }

    fun getSelections(): List<CatalogSticker> =
        getByCategory(StickerCategory.SELECAO)

    fun getPlayers(): List<CatalogSticker> =
        getByCategory(StickerCategory.JOGADOR)

    /** Raridade oficial da figurinha. COMMON apenas para ids fora do catalogo. */
    fun getRarity(stickerId: Int): StickerRarity =
        byId[stickerId]?.rarity ?: StickerRarity.COMMON

    fun getImageUrl(stickerId: Int): String =
        byId[stickerId]?.imageUrl.orEmpty()

    /** Sorteio uniforme sobre o catalogo. Usado como fonte de sorteio dos pacotes. */
    fun randomSticker(): CatalogSticker =
        allStickers.random()

    private data class SelecaoSeed(
        val teamId: Int,
        val name: String,
        val description: String
    )
}
