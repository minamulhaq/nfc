package com.opinions.nfc

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.opinions.nfc.navigation.Screens
import com.opinions.nfc.ui.screens.ReadScreen
import com.opinions.nfc.ui.screens.WriteScreen
import com.opinions.nfc.ui.theme.NfcTheme
import com.opinions.nfc.util.NfcIntentParser
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
    val navController = rememberNavController()
    val navHost = NavHost(navController = navController, startDestination = Screens.Read.route){
        composable(route = Screens.Read.route) {
            ReadScreen(modifier = modifier, readScreenViewModel = hiltViewModel(), goToRoute = {screen: Screens->
                navController.navigate(screen.route)
            })
        }

        composable(route = Screens.Write.route) {
            WriteScreen(writeScreenViewModel = hiltViewModel(), goToRoute = {screen: Screens->
                navController.navigate(screen.route)
            })
        }

    }
}