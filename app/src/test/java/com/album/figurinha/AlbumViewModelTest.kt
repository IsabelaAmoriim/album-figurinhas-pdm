package com.album.figurinha

import android.app.Application
import com.album.figurinha.model.StickerRarity
import com.album.figurinha.viewmodel.AlbumViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AlbumViewModelTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var fakeApplication: Application

    @Before
    fun setUp() {
        fakePrefs = FakeSharedPreferences()
        fakeApplication = Application()
    }

    @Test
    fun testEstadoInicialAlbumVazio() {
        val viewModel = AlbumViewModel(fakeApplication, fakePrefs)
        assertTrue(viewModel.collectedIds.value.isEmpty())
        assertEquals(0f, viewModel.getProgress(20), 0.001f)
    }

    @Test
    fun testAdicionarFigurinhaEProgresso() {
        val viewModel = AlbumViewModel(fakeApplication, fakePrefs)
        viewModel.addSticker(154, StickerRarity.LEGENDARY)

        assertTrue(viewModel.isCollected(154))
        assertEquals(StickerRarity.LEGENDARY, viewModel.getRarity(154))
        assertEquals(1, viewModel.collectedIds.value.size)
        assertEquals(0.05f, viewModel.getProgress(20), 0.001f)
    }

    @Test
    fun testAdicionarMultiplasFigurinhasEPersistencia() {
        val vm1 = AlbumViewModel(fakeApplication, fakePrefs)
        vm1.addStickers(
            listOf(
                Pair(614, StickerRarity.COMMON),
                Pair(732, StickerRarity.SPECIAL),
                Pair(154, StickerRarity.LEGENDARY)
            )
        )

        assertEquals(3, vm1.collectedIds.value.size)

        // Nova sessão recriando ViewModel com os mesmos SharedPreferences
        val vm2 = AlbumViewModel(fakeApplication, fakePrefs)
        assertEquals(3, vm2.collectedIds.value.size)
        assertTrue(vm2.isCollected(614))
        assertTrue(vm2.isCollected(732))
        assertTrue(vm2.isCollected(154))
        assertEquals(StickerRarity.LEGENDARY, vm2.getRarity(154))
    }
}
