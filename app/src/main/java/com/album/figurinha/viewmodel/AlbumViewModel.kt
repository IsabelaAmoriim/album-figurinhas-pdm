package com.album.figurinha.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.album.figurinha.model.StickerRarity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlbumViewModel @JvmOverloads constructor(
    application: Application,
    private val prefs: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
) : AndroidViewModel(application) {

    private val _collectedIds = MutableStateFlow(loadCollectedIds())
    val collectedIds: StateFlow<Set<Int>> = _collectedIds.asStateFlow()

    private val _raritiesMap = MutableStateFlow(loadRaritiesMap())
    val raritiesMap: StateFlow<Map<Int, StickerRarity>> = _raritiesMap.asStateFlow()

    private fun loadCollectedIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_COLLECTED_IDS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun loadRaritiesMap(): Map<Int, StickerRarity> {
        val stringSet = prefs.getStringSet(KEY_RARITIES, emptySet()) ?: emptySet()
        val map = mutableMapOf<Int, StickerRarity>()
        for (entry in stringSet) {
            val parts = entry.split(":")
            if (parts.size == 2) {
                val id = parts[0].toIntOrNull()
                val rarity = try { StickerRarity.valueOf(parts[1]) } catch (e: Exception) { StickerRarity.COMMON }
                if (id != null) {
                    map[id] = rarity
                }
            }
        }
        return map
    }

    private fun saveState(ids: Set<Int>, rarities: Map<Int, StickerRarity>) {
        val idsStrings = ids.map { it.toString() }.toSet()
        val rarityStrings = rarities.map { "${it.key}:${it.value.name}" }.toSet()
        prefs.edit()
            .putStringSet(KEY_COLLECTED_IDS, idsStrings)
            .putStringSet(KEY_RARITIES, rarityStrings)
            .apply()
    }

    fun isCollected(playerId: Int): Boolean {
        return _collectedIds.value.contains(playerId)
    }

    fun getRarity(playerId: Int): StickerRarity {
        return _raritiesMap.value[playerId] ?: StickerRarity.COMMON
    }

    fun addSticker(playerId: Int, rarity: StickerRarity) {
        _collectedIds.update { currentIds ->
            val newIds = currentIds + playerId
            _raritiesMap.update { currentRarities ->
                val existingRarity = currentRarities[playerId]
                val newRarity = if (existingRarity == null || rarity.ordinal > existingRarity.ordinal) {
                    rarity
                } else {
                    existingRarity
                }
                val newRarities = currentRarities + (playerId to newRarity)
                saveState(newIds, newRarities)
                newRarities
            }
            newIds
        }
    }

    fun addStickers(stickers: List<Pair<Int, StickerRarity>>) {
        _collectedIds.update { currentIds ->
            val newIds = currentIds + stickers.map { it.first }
            _raritiesMap.update { currentRarities ->
                val newRarities = currentRarities.toMutableMap()
                for ((id, rarity) in stickers) {
                    val existing = newRarities[id]
                    if (existing == null || rarity.ordinal > existing.ordinal) {
                        newRarities[id] = rarity
                    }
                }
                saveState(newIds, newRarities)
                newRarities
            }
            newIds
        }
    }

    fun getProgress(totalPlayers: Int = 20): Float {
        if (totalPlayers <= 0) return 0f
        return (_collectedIds.value.size.toFloat() / totalPlayers).coerceIn(0f, 1f)
    }

    companion object {
        const val PREFS_NAME = "album_prefs"
        const val KEY_COLLECTED_IDS = "collected_ids"
        const val KEY_RARITIES = "rarities_map"
    }
}
