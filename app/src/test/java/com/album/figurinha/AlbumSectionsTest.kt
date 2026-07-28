package com.album.figurinha

import android.app.Application
import com.album.figurinha.model.StickerCategory
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.repository.StickerCatalog
import com.album.figurinha.viewmodel.AlbumViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/** Agrupamento por categoria que alimenta o grid da tela do álbum (#8). */
class AlbumSectionsTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var fakeApplication: Application

    private val messi = 154

    @Before
    fun setUp() {
        fakePrefs = FakeSharedPreferences()
        fakeApplication = Application()
    }

    private fun newAlbum() =
        AlbumViewModel(
            fakeApplication,
            fakePrefs
        )

    @Test
    fun testSectionsCobremTodoOCatalogo() {
        val sections = newAlbum().sections.value

        assertEquals(
            StickerCatalog.getTotalCount(),
            sections.sumOf { it.total }
        )
    }

    @Test
    fun testSectionsAgrupamPorCategoria() {
        val sections = newAlbum().sections.value

        val categorias = sections.map {
            it.category
        }

        assertTrue(
            categorias.contains(
                StickerCategory.SELECAO
            )
        )

        assertTrue(
            categorias.contains(
                StickerCategory.JOGADOR
            )
        )

        sections.forEach { section ->
            assertTrue(
                section.slots.all {
                    it.sticker.category == section.category
                }
            )
        }
    }

    @Test
    fun testAlbumNovoTemTodosOsSlotsBloqueados() {
        val sections = newAlbum().sections.value

        assertTrue(
            sections.all { section ->
                section.slots.none {
                    it.unlocked
                }
            }
        )

        assertEquals(
            0,
            sections.sumOf {
                it.collected
            }
        )
    }

    @Test
    fun testSlotRefleteQuantidadeERepetida() {
        val album = newAlbum()

        repeat(2) {
            album.addSticker(
                messi,
                StickerRarity.LEGENDARY
            )
        }

        val slot = album.sections.value
            .flatMap {
                it.slots
            }
            .first {
                it.sticker.id == messi
            }

        assertTrue(slot.unlocked)
        assertTrue(slot.repeated)
        assertEquals(
            2,
            slot.quantity
        )
    }

    @Test
    fun testSectionsAtualizamAposVenda() {
        val album = newAlbum()

        val wallet =
            com.album.figurinha.viewmodel.WalletViewModel(
                fakeApplication,
                fakePrefs
            )

        repeat(3) {
            album.addSticker(
                messi,
                StickerRarity.LEGENDARY
            )
        }

        assertTrue(
            album.sellStickers(
                messi,
                2,
                wallet
            )
        )

        val slot = album.sections.value
            .flatMap {
                it.slots
            }
            .first {
                it.sticker.id == messi
            }

        assertEquals(
            1,
            slot.quantity
        )

        assertTrue(
            slot.unlocked
        )

        assertFalse(
            slot.repeated
        )
    }
}
