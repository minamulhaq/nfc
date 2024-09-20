package com.example.nfc.util

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import com.example.nfc.model.NFCInformation
import com.example.nfc.model.NfcAppMode
import com.example.nfc.model.NfcAppModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class NfcIntentParser @Inject constructor(
    private val _nfcAppModeManager: NfcAppModeManager
) {
    private val TAG: String = "NfcIntentParser"
    private val _nfcPayload = MutableStateFlow<MutableList<NFCInformation>>(mutableListOf())
    private val _nfcNdefMessageParser: NFCNdefMessageParser = NFCNdefMessageParser()
    private val _nfcAppMode = _nfcAppModeManager.nfcAppMode

    val nfcPayload: StateFlow<MutableList<NFCInformation>> = _nfcPayload




    private val _detectedTag: MutableStateFlow<Tag?> = MutableStateFlow(null)
    val detectedTag: StateFlow<Tag?> = _detectedTag.asStateFlow()

//    fun setNfcAppMode(mode: NfcAppMode) {
//        _nfcAppModeManager.setNfcAppMode(mode)
//    }

    fun createNDefMessageFromString(string: String): NdefMessage {
        val record = NdefRecord.createTextRecord("en", string)
        return NdefMessage(arrayOf(record))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onNewIntent(intent: Intent) {
        val tagInformation = _nfcNdefMessageParser.getNFCTagInformation(intent)
        Log.d(TAG, "onNewIntent: $tagInformation")

        _detectedTag.value = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG, Tag::class.java)
        try {
            
        } catch (e: Exception) {
            Log.d(TAG, "onNewIntent: ${e.message}")
        }


        val validActions = listOf(
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED
        )

        if (intent.action in validActions) {
            when(_nfcAppMode.value) {
                is NfcAppMode.FINISHED -> {
                    
                }
                is NfcAppMode.READ -> {
                    readFromNFCDevice(intent)
                }
                is NfcAppMode.WRITING -> {
                    writeToNFCDevice(intent)
                    
                }
                is NfcAppMode.ERROR -> {}
            }

        }
    }

    private fun writeToNFCDevice(intent: Intent) { Log.d(TAG, "writeToNFCDevice: Writing NFC")

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun readFromNFCDevice(intent: Intent) {
        Log.d(TAG, "readFromNFCDevice: Reading from NFC Device")
        _nfcPayload.value.clear()
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
            ?.also { rawMessages ->
                _nfcPayload.value.addAll(rawMessages.map {
                    NFCInformation(
                        msg = it as NdefMessage,
                        tagType = _nfcNdefMessageParser.getNFCTagInformation(intent),
                        supportedTechs = _nfcNdefMessageParser.getAvailableTechnologies(intent)
                    )
                })
            }
        // Unknown tag
        if (_nfcPayload.value.isEmpty()) {
            _nfcPayload.value.add(_nfcNdefMessageParser.createNDefFromUnknownTag(intent))
        }
    }


    fun updateAppMode(mode: NfcAppMode) {
        _nfcAppModeManager.updateNfcAppMode(mode)
    }

}
