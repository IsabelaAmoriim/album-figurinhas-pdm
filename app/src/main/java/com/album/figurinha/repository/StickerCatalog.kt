package com.album.figurinha.repository

import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.model.Team

/**
 * Catalogo unico de figurinhas do album.
 *
 * Antes populado estaticamente, agora e preenchido pelos dados da API via
 * [loadFromRepository].
 *
 * IDs das selecoes sao deslocados por [SELECTION_ID_OFFSET] para que nunca
 * colidam com ids de jogadores vindos da API-Sports (que sao inteiros baixos).
 */
object StickerCatalog {

    const val SELECTION_ID_OFFSET = 10_000

    fun selectionStickerId(teamId: Int): Int =
        SELECTION_ID_OFFSET + teamId

    fun teamIdFromSelectionSticker(stickerId: Int): Int =
        stickerId - SELECTION_ID_OFFSET

    @Volatile
    private var _allStickers: List<CatalogSticker> = emptyList()

    @Volatile
    private var _byId: Map<Int, CatalogSticker> = emptyMap()

    @Volatile
    private var _loading = false

    val isLoading: Boolean get() = _loading

    /** Todas as figurinhas do album. */
    val allStickers: List<CatalogSticker> get() = _allStickers

    /**
     * Popula o catalogo a partir de um [FootballRepository.LoadResult] ja
     * carregado. Metodo principal de inicializacao.
     */
    fun populateFrom(result: FootballRepository.LoadResult) {
        if (_allStickers.isNotEmpty()) return

        val selecoes = result.teams.map { team ->
            CatalogSticker(
                id = selectionStickerId(team.id),
                name = team.name,
                category = StickerCategory.SELECAO,
                rarity = StickerRarity.COMMON,
                imageUrl = team.shield,
                teamId = team.id,
                number = 0,
                position = "SELEÇÃO",
                description = buildSelectionDescription(team)
            )
        }

        val jogadores = result.teams.flatMap { team ->
            team.players.map { player ->
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
        }

        _allStickers = selecoes + jogadores
        _byId = _allStickers.associateBy { it.id }
        android.util.Log.d("StickerCatalog", "Catalog populated: ${_allStickers.size} stickers (${selecoes.size} teams, ${jogadores.size} players)")
    }

    /**
     * Carrega o catalogo a partir do [FootballRepository].
     * Deve ser chamado uma vez no inicio do app.
     */
    suspend fun loadFromRepository(repository: FootballRepository) {
        if (_allStickers.isNotEmpty() || _loading) return
        _loading = true
        try {
            val result = repository.loadAllWorldCupData()
            populateFrom(result)
        } finally {
            _loading = false
        }
    }

    private fun buildSelectionDescription(team: Team): String {
        val parts = mutableListOf<String>()
        if (team.titles > 0) parts.add("${team.titles} titulo(s) mundial(is)")
        parts.add("${team.players.size} jogadores")
        return parts.joinToString(" · ")
    }

    fun getById(stickerId: Int): CatalogSticker? = _byId[stickerId]

    fun contains(stickerId: Int): Boolean = _byId.containsKey(stickerId)

    fun getTotalCount(): Int = _allStickers.size

    fun getByCategory(category: StickerCategory): List<CatalogSticker> =
        _allStickers.filter { it.category == category }

    fun getSelections(): List<CatalogSticker> =
        getByCategory(StickerCategory.SELECAO)

    fun getPlayers(): List<CatalogSticker> =
        getByCategory(StickerCategory.JOGADOR)

    fun getRarity(stickerId: Int): StickerRarity =
        _byId[stickerId]?.rarity ?: StickerRarity.COMMON

    fun getImageUrl(stickerId: Int): String =
        _byId[stickerId]?.imageUrl.orEmpty()

    fun randomSticker(): CatalogSticker =
        _allStickers.random()

    /** Reseta o catalogo (util para testes ou troca de competicao). */
    fun reset() {
        _allStickers = emptyList()
        _byId = emptyMap()
    }
}
