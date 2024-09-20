package com.example.nfc.model

import android.nfc.Tag
import android.nfc.tech.NfcA
import android.util.Log

interface NfcWriter {
    val TAG: String
    fun write(tag: Tag, data: String): Boolean
}




class NfcAWriter: NfcWriter {
    override val TAG: String = "NfcAWriter"

    override fun write(tag: Tag, data: String):Boolean{
        val dataBytes = data.toByteArray(Charsets.UTF_8)
        val nfcA = NfcA.get(tag)
        return try {
            nfcA.connect()
            if (!nfcA.isConnected) {
                Log.d(TAG, "write: NFCA is not Connected")
                return false
            }
            Log.d(TAG, "write: NFCA is Connected")

            val writeCommand = byteArrayOf(0xA2.toByte(), 0x04.toByte()) // Example write command
            val payload = writeCommand + dataBytes // Concatenate write command with data

            nfcA.transceive(payload)
            return true
        } catch (e: Exception) {
            false
        } finally {
            try {
                nfcA.close()
            } catch (e: Exception){
                Log.d(TAG, "write: ${e.stackTraceToString()}")
            }
        }
    }

}