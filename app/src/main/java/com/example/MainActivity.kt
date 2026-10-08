package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DocumentsListScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LetterEditorScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.PaperBackground
import com.example.ui.theme.VoiceLetterTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLetterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceLetterTheme {
                Surface(
                    color = PaperBackground,
                    modifier = Modifier.fillMaxSize()
                ) {
                    VoiceLetterApp()
                }
            }
        }
    }
}

@Composable
fun VoiceLetterApp(
    viewModel: VoiceLetterViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(viewModel = viewModel)
        }
        AppScreen.EDITOR -> {
            LetterEditorScreen(viewModel = viewModel)
        }
        AppScreen.DOCUMENTS -> {
            DocumentsListScreen(viewModel = viewModel)
        }
        AppScreen.TEMPLATES -> {
            TemplatesScreen(viewModel = viewModel)
        }
    }
}
