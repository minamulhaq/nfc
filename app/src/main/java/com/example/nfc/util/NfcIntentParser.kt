package com.example.nfc.util

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.nfc.model.NFCInformation
import com.example.nfc.model.NfcAppMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NfcIntentParser @Inject constructor() {
    private val TAG: String = "NfcIntentParser"
    private val _nfcPayload = MutableStateFlow<MutableList<NFCInformation>>(mutableListOf())
    private val _nfcNdefMessageParser: NFCNdefMessageParser = NFCNdefMessageParser()

    val nfcPayload: StateFlow<MutableList<NFCInformation>> = _nfcPayload
    private var _nfcAction: NfcAppMode = NfcAppMode.READ()


    fun setNfcAction(action: NfcAppMode) {
        _nfcAction = action
    }

    fun createNDefMessageFromString(string: String): NdefMessage {
        val record = NdefRecord.createTextRecord("en", string)
        return NdefMessage(arrayOf(record))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onNewIntent(intent: Intent, nfcAppMode: NfcAppMode) {
        val tagInformation = _nfcNdefMessageParser.getNFCTagInformation(intent)
        Log.d(TAG, "onNewIntent: $tagInformation")


        val validActions = listOf(
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED
        )

        if (intent.action in validActions) {
            _nfcPayload.value.clear()
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
                ?.also { rawMessages ->
                    _nfcPayload.value.addAll(rawMessages.map {
                        NFCInformation(msg = it as NdefMessage, tagType = _nfcNdefMessageParser.getNFCTagInformation(intent), supportedTechs = _nfcNdefMessageParser.getAvailableTechnologies(intent))
                    })
                }
            // Unknown tag
            if (_nfcPayload.value.isEmpty()) {
                _nfcPayload.value.add(_nfcNdefMessageParser.createNDefFromUnknownTag(intent))
            }
        }
    }

}
