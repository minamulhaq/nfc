package com.example.nfc.util

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NfcIntentParser @Inject constructor() {
    private val TAG: String = "NfcIntentParser"
    private val _nfcPayload = MutableStateFlow<String>("No NFC data yet")
    val nfcPayload: StateFlow<String> = _nfcPayload

    fun onIntent(intent: Intent) {
        Log.d(TAG, "onIntent: ${intent.action}")

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.d(TAG, "onIntent: $tag")
            tag?.let {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    val ndefMessage = ndef.cachedNdefMessage
                    ndefMessage?.records?.forEach { record ->
                        val payload = String(record.payload)
                        Log.d(TAG, "NFC Payload: $payload")

                        // Update the flow with the new NFC data
                        _nfcPayload.value = payload
                    }
                } else {
                    Log.d(TAG, "No NDEF message on tag")
                    _nfcPayload.value = "No NDEF message on tag"
                }
            }
        } else {
            Log.d(TAG, "Unknown NFC action or tag")
            _nfcPayload.value = "Unknown NFC action or tag"
        }
    }
}
