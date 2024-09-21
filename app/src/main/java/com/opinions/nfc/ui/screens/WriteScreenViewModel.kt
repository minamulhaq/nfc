package com.opinions.nfc.ui.screens

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opinions.nfc.model.NFCManager
import com.opinions.nfc.model.NfcAppMode
import com.opinions.nfc.model.NfcAppModeManager
import com.opinions.nfc.model.NfcNdefWriter
import com.opinions.nfc.util.NfcIntentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteScreenViewModel @Inject constructor(
    private val _nfcManager: NFCManager,
    private val _nfcIntentParser: NfcIntentParser,
    private val _nfcAppModeManager: NfcAppModeManager
): ViewModel() {

    private val TAG: String = "WriteScreenViewModel"
    val nfcState = _nfcManager.nfcState
    val nfcPayload = _nfcIntentParser.nfcPayload
    val nfcAppMode = _nfcAppModeManager.nfcAppMode
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
            Log.d(TAG, "writeToTag: Success")
            _nfcAppModeManager.updateNfcAppMode(NfcAppMode.FINISHED())
        } else {
            Log.d(TAG, "writeToTag: Error")
            _nfcAppModeManager.updateNfcAppMode(NfcAppMode.ERROR())
        }
    }

    fun updateNfcAppMode(mode: NfcAppMode) {
        _nfcIntentParser.updateAppMode(mode)
    }


    fun writeStringPayload(textPayload: String) {
        _textPayloadToWrite = textPayload
        _nfcAppModeManager.updateNfcAppMode(NfcAppMode.WRITING())

    }
}