package com.example.nfc.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.nfc.model.NfcAppMode
import com.example.nfc.navigation.Screens
import com.example.nfc.util.NFCNdefMessageParser

@Composable
fun ReadScreen(
    modifier: Modifier = Modifier,
    readScreenViewModel: ReadScreenViewModel,
    goToRoute: (Screens) -> Unit
) {
    val TAG: String = "ReadScreen"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val nfcState = readScreenViewModel.nfcState.collectAsState()
    val nfcPayLoad = readScreenViewModel.nfcPayload.collectAsState()
    val nfcNdefMessageParser: NFCNdefMessageParser = NFCNdefMessageParser()
    val nfcAppMode = readScreenViewModel.nfcAppMode.collectAsState()






    DisposableEffect(lifecycleOwner) {
        // Create a LifecycleObserver
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    readScreenViewModel.onCreate(context = context as Activity)
                }

                Lifecycle.Event.ON_START -> {
                    readScreenViewModel.onStart()
                }

                Lifecycle.Event.ON_RESUME -> {
                    readScreenViewModel.onResume(context = context as Activity)
                }

                Lifecycle.Event.ON_PAUSE -> {
                    readScreenViewModel.onPause(context = context as Activity)
                }

                Lifecycle.Event.ON_STOP -> {
                    readScreenViewModel.onStop()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    readScreenViewModel.onDestroy()
                }

                Lifecycle.Event.ON_ANY -> {}
            }
        }
        // Add the observer to the lifecycle
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)

        // Cleanup when the composable leaves the composition
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }



    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(text = "READ")
            Text(text = nfcState.value.name)
            nfcPayLoad.value.forEachIndexed { i, nfcInformation ->
                Text(text = "Record: $i")
                Text(text = "TagType: ${nfcInformation.tagType}")
                Text(text = "Supported Techs: ${nfcInformation.supportedTechs}")
                nfcNdefMessageParser.parseNdefMessage(nfcInformation.msg).forEach {
                    Text(text = it)
                }
            }
            Button(onClick = {
                readScreenViewModel.updateNfcAppMode(NfcAppMode.WRITING())
                goToRoute(Screens.Write)
            }) {
                Text(text = "Go To Write Screen")
            }
        }
    }


}