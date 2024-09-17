package com.example.nfc.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.nfc.model.NfcAppMode
import com.example.nfc.util.NFCNdefMessageParser

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainScreenViewModel: MainScreenViewModel,
    nfcAppModeCallback: (NfcAppMode)-> Unit

) {
    val TAG: String = "MainScreen"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val nfcState = mainScreenViewModel.nfcState.collectAsState()
    val nfcPayLoad = mainScreenViewModel.nfcPayload.collectAsState()
    val nfcNdefMessageParser: NFCNdefMessageParser = NFCNdefMessageParser()
    val nfcAppMode = mainScreenViewModel.nfcAppMode.collectAsState()






    DisposableEffect(lifecycleOwner) {
        // Create a LifecycleObserver
        val observer = LifecycleEventObserver { source, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> {
                    mainScreenViewModel.onCreate(context = context as Activity)
                }
                Lifecycle.Event.ON_START -> {
                    mainScreenViewModel.onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
                    mainScreenViewModel.onResume(context = context as Activity)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mainScreenViewModel.onPause(context = context as Activity)
                }
                Lifecycle.Event.ON_STOP -> {
                    mainScreenViewModel.onStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    mainScreenViewModel.onDestroy()
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
    LaunchedEffect(nfcPayLoad) {

    }
    var textToWrite by remember {
        mutableStateOf("")
    }


    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            Text(text = nfcState.value.name)
            nfcPayLoad.value.forEachIndexed {i, nfcInformation ->
                Text(text = "Record: $i")
                Text(text = "TagType: ${nfcInformation.tagType}")
                Text(text= "Supported Techs: ${nfcInformation.supportedTechs}")
                nfcNdefMessageParser.parseNdefMessage(nfcInformation.msg).forEach{
                    Text(text = it)
                }
            }
            TextField(
                value = textToWrite,
                onValueChange = {
                    textToWrite = it
                },
                label = { Text(text = "Enter text to write") }
            )
            Text(text = "App Mode: ${nfcAppMode.value.description}")
            Button(
                onClick = {
                    mainScreenViewModel.writeTextToNFC(textToWrite)
                    nfcAppModeCallback (nfcAppMode.value)
                }
            ) {
                Text(text = "Write")
            }
        }
    }


}