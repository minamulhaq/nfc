package com.example.nfc.ui.screens

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.nfc.model.NFCManager
import com.example.nfc.util.NfcIntentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val nfcManager: NFCManager,
    private  val nfcIntentParser: NfcIntentParser
): ViewModel() {
    private val TAG: String = "MainScreenViewModel"

    val nfcState = nfcManager.nfcState
    val nfcPayload = nfcIntentParser.nfcPayload


    override fun onCleared() {
        super.onCleared()
        nfcManager.unregisterReceiver()
    }

    fun onCreate(context: Activity) {
        Log.d(TAG, "onCreate: ")
        nfcManager.getNfcAdapter(context)
    }

    fun onStart() {
        Log.d(TAG, "onStart: ")
    }

    fun onResume(context: Activity) {
        Log.d(TAG, "onResume: ")
        nfcManager.detectNFCState()
        nfcManager.registerNFCStateChanged()
        nfcManager.registerNfcForegroundDispatch(context)
        nfcManager.enableForegroundDispatch(context)
    }

    fun onPause(context: Activity) {
        Log.d(TAG, "onPause: ")
        nfcManager.disableForegroundDispatch(context)

    }

    fun onStop() {
        Log.d(TAG, "onStop: ")
    }

    fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        nfcManager.unregisterReceiver()
    }


    fun handleNfcIntent(intent: Intent?) {
//        nfcManager.handleNFCIntent(intent) { nfcData ->
//            _nfcData.value = nfcData
//        }
    }
}