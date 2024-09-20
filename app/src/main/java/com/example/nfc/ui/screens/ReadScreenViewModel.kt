package com.example.nfc.ui.screens

import android.app.Activity
import android.content.Intent
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfc.model.NFCManager
import com.example.nfc.model.NfcAppMode
import com.example.nfc.model.NfcAppModeManager
import com.example.nfc.model.NfcNdefWriter
import com.example.nfc.util.NfcIntentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadScreenViewModel @Inject constructor(
    private val _nfcManager: NFCManager,
    private val _nfcIntentParser: NfcIntentParser,
    private val _nfcAppModeManager: NfcAppModeManager
) : ViewModel() {
    private val TAG: String = "MainScreenViewModel"
    val nfcState = _nfcManager.nfcState
    val nfcPayload = _nfcIntentParser.nfcPayload
    val nfcAppMode = _nfcAppModeManager.nfcAppMode





    override fun onCleared() {
        super.onCleared()
        _nfcManager.unregisterReceiver()
    }

    fun onCreate(context: Activity) {
        Log.d(TAG, "onCreate: ")
        _nfcManager.getNfcAdapter(context)
    }

    fun onStart() {
        Log.d(TAG, "onStart: ")
    }

    fun onResume(context: Activity) {
        Log.d(TAG, "onResume: ")
        _nfcManager.detectNFCState()
        _nfcManager.registerNFCStateChanged()
        _nfcManager.registerNfcForegroundDispatch(context)
        _nfcManager.enableForegroundDispatch(context)
    }

    fun onPause(context: Activity) {
        Log.d(TAG, "onPause: ")
        _nfcManager.disableForegroundDispatch(context)

    }

    fun onStop() {
        Log.d(TAG, "onStop: ")
    }

    fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        _nfcManager.unregisterReceiver()
    }


    fun handleNfcIntent(intent: Intent?) {
//        nfcManager.handleNFCIntent(intent) { nfcData ->
//            _nfcData.value = nfcData
//        }
    }


    fun updateNfcAppMode(mode: NfcAppMode) {
        _nfcIntentParser.updateAppMode(mode)
    }
}