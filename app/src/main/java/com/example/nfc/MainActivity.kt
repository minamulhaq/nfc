package com.example.nfc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nfc.ui.screens.MainScreen
import com.example.nfc.ui.screens.MainScreenViewModel
import com.example.nfc.ui.theme.NfcTheme
import com.example.nfc.util.NfcIntentParser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"

    @Inject
    lateinit var nfcIntentParser: NfcIntentParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NfcTheme {
                NFCApplication()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ${intent.action}")
        nfcIntentParser.onIntent(intent)
    }
}

@Composable
fun NFCApplication(modifier: Modifier = Modifier) {
    MainScreen(modifier = modifier, mainScreenViewModel = hiltViewModel())
}