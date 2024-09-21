package com.example.nfc.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.nfc.model.NfcAppMode
import com.example.nfc.navigation.Screens
import com.example.nfc.util.NFCNdefMessageParser
import kotlinx.coroutines.launch

@Composable
fun WriteScreen(
    modifier: Modifier = Modifier,
    writeScreenViewModel: WriteScreenViewModel,
    goToRoute: (Screens) -> Unit
) {
    val TAG: String = "WriteScreen"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val nfcState = writeScreenViewModel.nfcState.collectAsState()
    val nfcPayLoad = writeScreenViewModel.nfcPayload.collectAsState()
    val nfcNdefMessageParser: NFCNdefMessageParser = NFCNdefMessageParser()
    val nfcAppMode = writeScreenViewModel.nfcAppMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current


    var textToWrite by remember {
        mutableStateOf("")
    }

    LaunchedEffect(nfcAppMode.value) {
        Log.d(TAG, "WriteScreen: NFCAPPMODECHANGED, ${nfcAppMode.value}")
        when (nfcAppMode.value) {
            is NfcAppMode.ERROR -> {}
            is NfcAppMode.FINISHED -> {
                Log.d(TAG, "WriteScreen: Value is finished")
                textToWrite = ""
                snackbarHostState.showSnackbar(
                    message = "NFC Write Success, Go back To Read Screen to verify TAG"
                )
                writeScreenViewModel.updateNfcAppMode(NfcAppMode.READ())

            }

            is NfcAppMode.READ -> {}
            is NfcAppMode.WRITING -> {}
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->


        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Write Screen")
            TextField(
                value = textToWrite,
                onValueChange = {
                    textToWrite = it
                },
                label = { Text(text = "Enter text to write to the TAG") }
            )
            Text(text = "App Mode: ${nfcAppMode.value.description}")
            Button(
                onClick = {
                    writeScreenViewModel.writeStringPayload(textToWrite)
                    keyboardController?.hide()
                    scope.launch {
                        snackbarHostState.showSnackbar("Bring NFC Tag near the device")
                    }
                },
                enabled = textToWrite.isNotBlank()
            ) {
                Text(text = "Write TEXT record")
            }
        }
    }

}