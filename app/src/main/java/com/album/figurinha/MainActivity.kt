package com.album.figurinha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.album.figurinha.ui.screens.CompetitionScreen
import com.album.figurinha.ui.theme.FigurinhaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FigurinhaTheme {
                CompetitionScreen()
            }
        }
    }
}