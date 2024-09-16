package com.example.nfc.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainScreenViewModel: MainScreenViewModel
) {
    val TAG: String = "MainScreen"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val nfcState = mainScreenViewModel.nfcState.collectAsState()
    val nfcPayLoad = mainScreenViewModel.nfcPayload.collectAsState()





    DisposableEffect(lifecycleOwner) {
        // Create a LifecycleObserver
        val observer = LifecycleEventObserver { source, event ->
            Log.d(TAG, "MainScreen: Source of lifecycle event: $source")
            when(event) {
                Lifecycle.Event.ON_CREATE -> {
                    mainScreenViewModel.onCreate(context as Activity)
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

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = nfcState.value.name)
            Text(text = nfcPayLoad.value)
        }
    }


}