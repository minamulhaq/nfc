package com.opinions.nfc.model

import android.nfc.NdefMessage
import android.nfc.NdefRecord.createTextRecord
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.util.Log

interface NfcWriter {
    val TAG: String
    fun write(tag: Tag, data: String): Boolean
}




class NfcNdefWriter: NfcWriter {
    override val TAG: String = "NfcNdefWriter"

    override fun write(tag: Tag, data: String): Boolean {
        val ndef = Ndef.get(tag)

        return try {
            if (ndef != null) {
                // Create an NDEF record with the data as a text record
                val ndefRecord = createTextRecord("en", data)
                val ndefMessage = NdefMessage(arrayOf(ndefRecord))

                // Connect to the tag and write the message
                ndef.connect()
                if (!ndef.isWritable) {
                    Log.e(TAG, "NFC tag is not writable")
                    return false
                }

                val size = ndefMessage.toByteArray().size
                if (ndef.maxSize < size) {
                    Log.e(TAG, "NFC tag does not have enough space")
                    return false
                }

                ndef.writeNdefMessage(ndefMessage)
                Log.d(TAG, "NDEF message written successfully")
                true
            } else {
                Log.e(TAG, "NFC tag is not NDEF compatible")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write NDEF message: ${e.localizedMessage}")
            false
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing NFC connection: ${e.localizedMessage}")
            }
        }
    }

}