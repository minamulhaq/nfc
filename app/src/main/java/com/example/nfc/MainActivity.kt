package com.example.nfc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nfc.model.NfcAppMode
import com.example.nfc.ui.screens.MainScreen
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcIntentParser.onNewIntent(intent)
    }


    private fun resolveIntent(intent: Intent) {

    }
}

@Composable
fun NFCApplication(modifier: Modifier = Modifier) {
    val name: String = ""
    MainScreen(modifier = modifier, mainScreenViewModel = hiltViewModel())
}