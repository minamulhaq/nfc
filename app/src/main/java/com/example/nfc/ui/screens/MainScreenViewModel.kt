package com.example.nfc.ui.screens

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.example.nfc.model.NFCManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val nfcManager: NFCManager
): ViewModel() {
    private val TAG: String = "MainScreenViewModel"

    val nfcState = nfcManager.nfcState
    fun onEvent(event: Lifecycle.Event) {
        nfcManager.onEvent(event)
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.d(TAG, "onEvent: ${event.toString()}")
            }
            Lifecycle.Event.ON_START -> Log.d(TAG, "onEvent: ${event.toString()}")
            Lifecycle.Event.ON_RESUME -> Log.d(TAG, "onEvent: ${event.toString()}")
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "onEvent: ${event.toString()}")
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "onEvent: ${event.toString()}")
            Lifecycle.Event.ON_DESTROY -> {
                Log.d(TAG, "onEvent: ${event.toString()}")
            }
            Lifecycle.Event.ON_ANY -> Log.d(TAG, "onEvent: ${event.toString()}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        nfcManager.unregisterReceiver()
    }

}