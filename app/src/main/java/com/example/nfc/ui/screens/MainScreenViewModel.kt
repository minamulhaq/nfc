package com.example.nfc.ui.screens

import android.app.Activity
import android.content.Intent
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfc.model.NFCManager
import com.example.nfc.model.NfcAppMode
import com.example.nfc.model.NfcNdefWriter
import com.example.nfc.util.NfcIntentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val _nfcManager: NFCManager,
    private val _nfcIntentParser: NfcIntentParser
) : ViewModel() {
    private val TAG: String = "MainScreenViewModel"
    val nfcState = _nfcManager.nfcState
    val nfcPayload = _nfcIntentParser.nfcPayload
    val nfcAppMode = _nfcIntentParser.nfcAppMode
    private var _textPayloadToWrite: String = ""


    init {
        viewModelScope.launch {
            val detectedTag = _nfcIntentParser.detectedTag.collectLatest {
                Log.d(TAG, "Tag Collected in viewmodel: $it")
                if (nfcAppMode.value is NfcAppMode.WRITING) {
                    Log.d(TAG, "App Mode: ${nfcAppMode.value}")
                    it?.also {
                        Log.d(TAG, "Tag Collected in viewmodel to write: $it")
                        writeToTag(it, _textPayloadToWrite)
                    }
                }

            }
        }

    }

    private fun writeToTag(it: Tag, data: String) {
        Log.d(TAG, "writeToTag: $data")
        val writer = NfcNdefWriter()
        val result = writer.write(it, data)
        if (result) {
            _nfcIntentParser.updateAppMode(NfcAppMode.FINISHED())
        } else {
            _nfcIntentParser.updateAppMode(NfcAppMode.ERROR())
        }
    }


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

    fun writeStringPayload(textPayload: String) {
        _textPayloadToWrite = textPayload
        _nfcIntentParser.setNfcAppMode(NfcAppMode.WRITING())

    }

    fun updateNfcAppMode(mode: NfcAppMode) {
        _nfcIntentParser.updateAppMode(mode)
    }
}