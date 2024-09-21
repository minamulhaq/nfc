package com.opinions.nfc.model

import android.nfc.NdefMessage

data class NFCInformation(
    val msg: NdefMessage,
    val tagType: String,
    val supportedTechs: List<String>
)
