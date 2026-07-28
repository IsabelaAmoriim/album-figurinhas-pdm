package com.album.figurinha.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.album.figurinha.model.CatalogSticker
import com.album.figurinha.model.ColecaoFigurinha
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.repository.StickerCatalog
import com.album.figurinha.repository.StickerPricing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Progresso do álbum, sempre calculado sobre o total do StickerCatalog. */
data class AlbumProgress(
    val collected: Int,
    val total: Int
) {
    val fraction: Float
        get() = if (total <= 0) {
            0f
        } else {
            (collected.toFloat() / total).coerceIn(0f, 1f)
        }

    val percentage: Int
        get() = (fraction * 100).toInt()
}

/** Um slot do álbum: a figurinha do catálogo + quanto o usuário possui. */
data class AlbumSlot(
    val sticker: CatalogSticker,
    val quantity: Int
) {
    val unlocked: Boolean
        get() = quantity > 0

    val repeated: Boolean
        get() = quantity > 1
}

/** Slots agrupados por categoria, na ordem em que o álbum exibe. */
data class AlbumSection(
    val category: StickerCategory,
    val slots: List<AlbumSlot>
) {
    val collected: Int
        get() = slots.count { it.unlocked }

    val total: Int
        get() = slots.size
}

/** Figurinha repetida, já com excedente vendável e valores calculados. */
data class RepeatedSticker(
    val sticker: CatalogSticker,
    val quantity: Int,
    val sellableQuantity: Int,
    val unitPrice: Int
) {
    val totalValue: Int
        get() = sellableQuantity * unitPrice
}

/**
 * Estado do álbum do usuário.
 *
 * PERSISTÊNCIA (SharedPreferences "album_prefs")
 * - "quantities_map" StringSet de "stickerId:quantidade" (schema v1)
 * - "schema_version" Int
 * - "collected_ids" StringSet de "stickerId" (schema v0, legado)
 * - "rarities_map" StringSet de "stickerId:RARIDADE" (schema v0, legado)
 *
 * As chaves legadas continuam sendo escritas de propósito: se alguém do grupo
 * voltar para um APK anterior, o álbum não some.
 *
 * MIGRAÇÃO v0 -> v1
 * Toda figurinha já coletada entra com quantity = 1. Logo após a atualização
 * ninguém tem repetida para vender, que é o comportamento correto e seguro.
 * A migração é idempotente e não apaga dado nenhum.
 */
class AlbumViewModel @JvmOverloads constructor(
    application: Application,
    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
) : AndroidViewModel(application) {

    private val _quantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quantities: StateFlow<Map<Int, Int>> = _quantities.asStateFlow()

    private val _collectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val collectedIds: StateFlow<Set<Int>> = _collectedIds.asStateFlow()

    private val _raritiesMap =
        MutableStateFlow<Map<Int, StickerRarity>>(emptyMap())
    val raritiesMap: StateFlow<Map<Int, StickerRarity>> =
        _raritiesMap.asStateFlow()

    private val _progress =
        MutableStateFlow(AlbumProgress(0, StickerCatalog.getTotalCount()))
    val progress: StateFlow<AlbumProgress> = _progress.asStateFlow()

    /** Grid do álbum agrupado por categoria (Seleções, Jogadores). */
    private val _sections =
        MutableStateFlow(buildSections(emptyMap()))
    val sections: StateFlow<List<AlbumSection>> =
        _sections.asStateFlow()

    private val _repeatedStickers =
        MutableStateFlow<List<RepeatedSticker>>(emptyList())
    val repeatedStickers: StateFlow<List<RepeatedSticker>> =
        _repeatedStickers.asStateFlow()

    /** RF21 - mensagem de erro para snackbar de falha. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** Mensagem de sucesso da venda, para snackbar. */
    private val _sellMessage = MutableStateFlow<String?>(null)
    val sellMessage: StateFlow<String?> = _sellMessage.asStateFlow()

    /** Trava o botão de venda enquanto a operação corre. */
    private val _isSelling = MutableStateFlow(false)
    val isSelling: StateFlow<Boolean> = _isSelling.asStateFlow()

    /** Seleções do catálogo, expostas para a UI não acessar o repositório direto. */
    val catalogSelections: List<CatalogSticker> =
        StickerCatalog.getSelections()

    init {
        val rarities =
            parseRarities(prefs.getStringSet(KEY_RARITIES, null))

        val quantities =
            loadQuantitiesMigratingIfNeeded(rarities)

        applyState(
            quantities,
            rarities,
            shouldPersist = false
        )
    }

    // ---------------------------------------------------------------------
    // Carga e migração
    // ---------------------------------------------------------------------

    private fun loadQuantitiesMigratingIfNeeded(
        rarities: Map<Int, StickerRarity>
    ): Map<Int, Int> {
        val version = prefs.getInt(
            KEY_SCHEMA_VERSION,
            SCHEMA_VERSION_LEGACY
        )

        val stored = prefs.getStringSet(KEY_QUANTITIES, null)

        if (version >= SCHEMA_VERSION_CURRENT && stored != null) {
            return parseQuantities(stored)
        }

        // Migração v0 -> v1: cada figurinha já coletada vira quantity = 1.
        val legacyIds = prefs.getStringSet(
            KEY_COLLECTED_IDS,
            null
        )
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }

        val migrated = legacyIds.associateWith { 1 }

        persist(migrated, rarities)

        return migrated
    }

    private fun parseQuantities(
        raw: Set<String>
    ): Map<Int, Int> =
        raw.mapNotNull { entry ->
            val parts = entry.split(":")

            if (parts.size != 2) {
                return@mapNotNull null
            }

            val id = parts[0].toIntOrNull()
                ?: return@mapNotNull null

            val quantity = parts[1].toIntOrNull()
                ?: return@mapNotNull null

            if (quantity <= 0) {
                null
            } else {
                id to quantity
            }
        }.toMap()

    private fun parseRarities(
        raw: Set<String>?
    ): Map<Int, StickerRarity> =
        raw.orEmpty().mapNotNull { entry ->
            val parts = entry.split(":")

            if (parts.size != 2) {
                return@mapNotNull null
            }

            val id = parts[0].toIntOrNull()
                ?: return@mapNotNull null

            val rarity = runCatching {
                StickerRarity.valueOf(parts[1])
            }.getOrDefault(
                StickerCatalog.getRarity(id)
            )

            id to rarity
        }.toMap()

    private fun persist(
        quantities: Map<Int, Int>,
        rarities: Map<Int, StickerRarity>
    ) {
        val quantityStrings = quantities
            .filterValues { it > 0 }
            .map { "${it.key}:${it.value}" }
            .toSet()

        val idStrings = quantities
            .filterValues { it > 0 }
            .keys
            .map { it.toString() }
            .toSet()

        val rarityStrings = rarities
            .map { "${it.key}:${it.value.name}" }
            .toSet()

        prefs.edit()
            .putStringSet(KEY_QUANTITIES, quantityStrings)
            .putInt(
                KEY_SCHEMA_VERSION,
                SCHEMA_VERSION_CURRENT
            )
            // Chaves legadas mantidas para compatibilidade com versões anteriores.
            .putStringSet(KEY_COLLECTED_IDS, idStrings)
            .putStringSet(KEY_RARITIES, rarityStrings)
            .apply()
    }

    private fun applyState(
        quantities: Map<Int, Int>,
        rarities: Map<Int, StickerRarity>,
        shouldPersist: Boolean
    ) {
        val sanitized = quantities.filterValues { it > 0 }

        _quantities.value = sanitized
        _raritiesMap.value = rarities
        _collectedIds.value = sanitized.keys.toSet()

        // Só conta figurinhas do catálogo: impede "coletadas > total".
        _progress.value = AlbumProgress(
            collected = sanitized.keys.count {
                StickerCatalog.contains(it)
            },
            total = StickerCatalog.getTotalCount()
        )

        _sections.value = buildSections(sanitized)
        _repeatedStickers.value = buildRepeated(sanitized)

        if (shouldPersist) {
            persist(sanitized, rarities)
        }
    }

    private fun buildSections(
        quantities: Map<Int, Int>
    ): List<AlbumSection> =
        StickerCategory.entries
            .map { category ->
                AlbumSection(
                    category = category,
                    slots = StickerCatalog
                        .getByCategory(category)
                        .map { sticker ->
                            AlbumSlot(
                                sticker = sticker,
                                quantity = quantities[sticker.id] ?: 0
                            )
                        }
                )
            }
            .filter { it.slots.isNotEmpty() }

    private fun buildRepeated(
        quantities: Map<Int, Int>
    ): List<RepeatedSticker> =
        quantities
            .filter {
                StickerPricing.isVendavel(it.value)
            }
            .mapNotNull { (id, quantity) ->
                val sticker = StickerCatalog.getById(id)
                    ?: return@mapNotNull null

                RepeatedSticker(
                    sticker = sticker,
                    quantity = quantity,
                    sellableQuantity =
                        StickerPricing.quantidadeVendavel(quantity),
                    unitPrice =
                        StickerPricing.precoPorRaridade(sticker.rarity)
                )
            }
            .sortedByDescending { it.totalValue }

    // ---------------------------------------------------------------------
    // Consulta
    // ---------------------------------------------------------------------

    fun isCollected(stickerId: Int): Boolean =
        (_quantities.value[stickerId] ?: 0) > 0

    fun getQuantity(stickerId: Int): Int =
        _quantities.value[stickerId] ?: 0

    fun getSellableQuantity(stickerId: Int): Int =
        StickerPricing.quantidadeVendavel(
            getQuantity(stickerId)
        )

    fun isRepeated(stickerId: Int): Boolean =
        StickerPricing.isVendavel(
            getQuantity(stickerId)
        )

    fun getRarity(stickerId: Int): StickerRarity =
        _raritiesMap.value[stickerId]
            ?: StickerCatalog.getRarity(stickerId)

    fun getProgress(): Float =
        _progress.value.fraction

    /** Coleção completa do usuário, incluindo os slots ainda bloqueados. */
    fun getCollection(): List<ColecaoFigurinha> =
        StickerCatalog.allStickers.map { sticker ->
            val quantity = getQuantity(sticker.id)

            ColecaoFigurinha(
                id = sticker.id,
                stickerId = sticker.id,
                quantity = quantity,
                unlocked = quantity > 0,
                repeated = StickerPricing.isVendavel(quantity)
            )
        }

    // ---------------------------------------------------------------------
    // Aquisição
    // ---------------------------------------------------------------------

    fun addSticker(
        stickerId: Int,
        rarity: StickerRarity
    ) = addStickers(
        listOf(stickerId to rarity)
    )

    /** Incrementa a quantidade de cada figurinha. Repetidas não são mais descartadas. */
    fun addStickers(
        stickers: List<Pair<Int, StickerRarity>>
    ) {
        if (stickers.isEmpty()) {
            return
        }

        val quantities = _quantities.value.toMutableMap()
        val rarities = _raritiesMap.value.toMutableMap()

        for ((stickerId, rarity) in stickers) {
            quantities[stickerId] =
                (quantities[stickerId] ?: 0) + 1

            val existing = rarities[stickerId]

            if (existing == null || rarity.ordinal > existing.ordinal) {
                rarities[stickerId] = rarity
            }
        }

        applyState(
            quantities,
            rarities,
            shouldPersist = true
        )
    }

    // ---------------------------------------------------------------------
    // Venda de repetidas (RF27 / RF34 / RF36)
    // ---------------------------------------------------------------------

    /**
     * Vende [quantity] unidades excedentes de [stickerId] e credita as moedas
     * na carteira existente. A coleção nunca cai abaixo de 1.
     */
    fun sellStickers(
        stickerId: Int,
        quantity: Int,
        walletViewModel: WalletViewModel
    ): Boolean {
        if (_isSelling.value) {
            return false
        }

        val sticker = StickerCatalog.getById(stickerId)

        if (sticker == null) {
            fail("Figurinha não encontrada no catálogo.")
            return false
        }

        val owned = getQuantity(stickerId)

        val sellable = StickerPricing.quantidadeVendavel(owned)

        if (sellable <= 0) {
            fail(
                "Você não tem repetidas de ${sticker.name} para vender."
            )
            return false
        }

        if (quantity !in 1..sellable) {
            fail(
                "Quantidade inválida: só é possível vender de 1 a $sellable unidade(s)."
            )
            return false
        }

        _isSelling.value = true

        try {
            val total = StickerPricing.valorTotal(
                sticker.rarity,
                quantity
            )

            val quantities = _quantities.value.toMutableMap()

            quantities[stickerId] = owned - quantity

            applyState(
                quantities,
                _raritiesMap.value,
                shouldPersist = true
            )

            walletViewModel.addCoins(total)

            _errorMessage.value = null

            _sellMessage.value =
                "$quantity figurinha(s) de ${sticker.name} vendida(s) por $total moedas."

            return true
        } finally {
            _isSelling.value = false
        }
    }

    /** Vende todo o excedente de todas as figurinhas repetidas de uma vez. */
    fun sellAllRepeated(
        walletViewModel: WalletViewModel
    ): Boolean {
        if (_isSelling.value) {
            return false
        }

        val repeated = _repeatedStickers.value

        if (repeated.isEmpty()) {
            fail("Você não tem figurinhas repetidas para vender.")
            return false
        }

        _isSelling.value = true

        try {
            val quantities = _quantities.value.toMutableMap()

            var totalCoins = 0
            var totalCards = 0

            for (item in repeated) {
                quantities[item.sticker.id] =
                    item.quantity - item.sellableQuantity

                totalCoins += item.totalValue
                totalCards += item.sellableQuantity
            }

            applyState(
                quantities,
                _raritiesMap.value,
                shouldPersist = true
            )

            walletViewModel.addCoins(totalCoins)

            _errorMessage.value = null

            _sellMessage.value =
                "$totalCards figurinha(s) vendida(s) por $totalCoins moedas."

            return true
        } finally {
            _isSelling.value = false
        }
    }

    /** Valor total que seria recebido vendendo todas as repetidas. */
    fun getTotalRepeatedValue(): Int =
        _repeatedStickers.value.sumOf {
            it.totalValue
        }

    fun clearMessages() {
        _errorMessage.value = null
        _sellMessage.value = null
    }

    private fun fail(message: String) {
        _sellMessage.value = null
        _errorMessage.value = message
    }

    companion object {
        const val PREFS_NAME = "album_prefs"
        const val KEY_COLLECTED_IDS = "collected_ids"
        const val KEY_RARITIES = "rarities_map"
        const val KEY_QUANTITIES = "quantities_map"
        const val KEY_SCHEMA_VERSION = "schema_version"

        const val SCHEMA_VERSION_LEGACY = 0
        const val SCHEMA_VERSION_CURRENT = 1
    }
}
