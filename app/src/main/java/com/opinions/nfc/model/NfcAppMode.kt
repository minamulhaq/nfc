package com.opinions.nfc.model

sealed class NfcAppMode(
    val description: String
) {
    class READ(description: String = "READ") : NfcAppMode(description = description)
    class WRITING(description: String = "WRITING"): NfcAppMode(description = description)
    class FINISHED(description: String = "FINISHED"): NfcAppMode(description = description)
    class ERROR(description: String = "ERROR"): NfcAppMode(description = description)
}
