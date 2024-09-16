package com.example.nfc.util

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.util.Log
import com.example.nfc.model.NFCInformation
import javax.inject.Inject

//val PendingIntent_Mutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//    PendingIntent.FLAG_MUTABLE
//} else {
//    0
//}
//
//inline fun <reified T> Intent.parcelable(key: String): T? {
//    setExtrasClassLoader(T::class.java.classLoader)
//    return when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
//        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
//    }
//}
//
//inline fun <reified T> Intent.parcelableArrayList(key: String): List<T>? {
//    setExtrasClassLoader(T::class.java.classLoader)
//    return when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayListExtra(key, T::class.java)
//        else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
//    }
//}
//
//inline fun <reified T> Bundle.parcelable(key: String): T? = when {
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
//}
//
//inline fun <reified T> Bundle.parcelableArrayList(key: String): List<T>? = when {
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayList(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
//}


class NFCNdefMessageParser @Inject constructor() {

    private val TAG: String = "NFCNdefMessageParser"

    fun parseNdefMessage(ndefMessage: NdefMessage): MutableList<String> {
        val records: MutableList<String> = mutableListOf()
        for (r in ndefMessage.records) {
            records.add(parseNdefRecord(r))
        }
        return records
    }

    fun getAvailableTechnologies(intent: Intent): List<String> {
        // Extract the NFC Tag from the Intent
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return emptyList()

        // Get the list of supported NFC technologies
        val techList = tag.techList

        // Convert the tech list to a human-readable format
        return techList.map { tech ->
            when (tech) {
                NfcA::class.java.name -> "NfcA"
                NfcB::class.java.name -> "NfcB"
                NfcF::class.java.name -> "NfcF"
                NfcV::class.java.name -> "NfcV"
                IsoDep::class.java.name -> "IsoDep"
                MifareClassic::class.java.name -> "MifareClassic"
                MifareUltralight::class.java.name -> "MifareUltralight"
                Ndef::class.java.name -> "Ndef"
                NdefFormatable::class.java.name -> "NdefFormatable"
                else -> "Unknown technology: $tech"
            }
        }
    }

    fun getNFCTagInformation(intent: Intent): String {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG, Tag::class.java)
            ?: return "Unknown tag type"
        // Get the list of supported NFC technologies
        val techList = tag.techList

        // Check if specific NFC technologies are supported and return the tag type accordingly
        return when {
            techList.contains(NfcA::class.java.name) -> "ISO 14443-3A (NFC-A)"
            techList.contains(NfcB::class.java.name) -> "ISO 14443-3B (NFC-B)"
            techList.contains(IsoDep::class.java.name) -> "ISO-DEP (ISO 14443-4)"
            techList.contains(NfcF::class.java.name) -> "FeliCa (NFC-F)"
            techList.contains(NfcV::class.java.name) -> "ISO 15693 (NFC-V)"
            techList.contains(MifareClassic::class.java.name) -> "MIFARE Classic"
            techList.contains(MifareUltralight::class.java.name) -> "MIFARE Ultralight"
            else -> "Unknown or Unsupported Tag Type"
        }
    }

    fun createNDefFromUnknownTag(intent: Intent): NFCInformation {
        val empty = ByteArray(0)
        val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG, Tag::class.java)
        val payload = tag?.let { dumpTagData(it).toByteArray() } ?: "Unknown tag".toByteArray()
        val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
        val msg = NdefMessage(arrayOf(record))
        val tagType = getNFCTagInformation(intent)
        val supportedTechs = getAvailableTechnologies(intent)
        return NFCInformation(msg = msg, tagType = tagType, supportedTechs = supportedTechs)
    }


    private fun parseNdefRecord(record: NdefRecord): String {
        when {
            record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT) -> {
                val text = parseTextRecord(record)
                Log.d(TAG, "parseNdefRecord: Parsed Text: $text")
                return text
            }

            record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_URI) -> {
                val uri = parseUriRecord(record)
                Log.d(TAG, "Parsed URI: $uri")
                return uri
            }

            else -> {
                val text = "Unknown NDEF record type"
                Log.d(TAG, "Unknown NDEF record type")
                return text
            }
        }
    }

    private fun parseTextRecord(record: NdefRecord): String {
        val payload = record.payload
        // Get text encoding
        val textEncoding = if (payload[0].toInt() and 0x80 == 0) "UTF-8" else "UTF-16"
        // Get language code length
        val languageCodeLength = payload[0].toInt() and 0x3F
        // Get the text
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            charset(textEncoding)
        )
    }

    private fun parseUriRecord(record: NdefRecord): String {
        val payload = record.payload
        // The first byte of the payload contains the URI prefix
        val prefix = getUriPrefix(payload[0])
        // The remaining bytes are the URI data
        val uri = String(payload, 1, payload.size - 1, Charsets.UTF_8)
        return prefix + uri
    }

    private fun getUriPrefix(prefixByte: Byte): String {
        return when (prefixByte.toInt()) {
            0x00 -> ""    // No prefix
            0x01 -> "http://www."
            0x02 -> "https://www."
            0x03 -> "http://"
            0x04 -> "https://"
            // Add more URI prefixes as needed
            else -> ""
        }
    }


    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.id
        sb.append("ID (hex): ").append(toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)

                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.appendLine("Mifare Classic type: $type")
                    sb.appendLine("Mifare size: ${mifareTag.size} bytes")
                    sb.appendLine("Mifare sectors: ${mifareTag.sectorCount}")
                    sb.appendLine("Mifare blocks: ${mifareTag.blockCount}")
                } catch (e: Exception) {
                    sb.appendLine("Mifare classic error: ${e.message}")
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }
        return sb.toString()
    }


    fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

    private fun toReversedHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            val b = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
        }
        return sb.toString()
    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value = bytes[i].toLong() and 0xffL
            result += value * factor
            factor *= 256L
        }
        return result
    }


    private fun toReversedDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].toLong() and 0xffL
            result += value * factor
            factor *= 256L
        }
        return result
    }

}

