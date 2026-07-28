package com.album.figurinha.model

/**
 * Categoria de uma figurinha do album.
 *
 * Observacao de modelagem: categoria e raridade sao dimensoes diferentes.
 * StickerRarity.COACHING mistura as duas (e categoria,  nao raridade) e esta
 * registrada como divida tecnica no README.
 */
enum class StickerCategory(val label: String) {
    SELECAO("Seleções"),
    JOGADOR("Jogadores")
}

/**
 * Item do catalogo de figurinhas.
 *
 * E a unica fonte de verdade de id, nome, categoria, raridade e imagem.
 * Ver [com.album.figurinha.repository.StickerCatalog].
 */
data class CatalogSticker(
    val id: Int,
    val name: String,
    val category: StickerCategory,
    val rarity: StickerRarity,
    val imageUrl: String,
    val teamId: Int,
    val number: Int = 0,
    val position: String = "",
    val description: String = ""
) {
    /**
     * Adapta a figurinha do catalogo para o model [Player] usado pelos
     * componentes de UI ja existentes (StickerCard, StoreScreen, telas de
     * detalhe). Evita duplicar componentes so por causa das selecoes.
     */
    fun toPlayer(): Player = Player(
        id = id,
        name = name,
        photo = imageUrl,
        number = number,
        position = position,
        description = description,
        teamId = teamId,
        rarity = rarity
    )
}
