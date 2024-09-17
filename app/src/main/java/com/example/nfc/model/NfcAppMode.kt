package com.example.nfc.model

sealed class NfcAppMode(
    val description: String
) {
    class READ(description: String = "READ") : NfcAppMode(description = description)
    class SEARCHING(description: String = "SEARCHING"): NfcAppMode(description = description)
    class WRITING(description: String = "WRITING"): NfcAppMode(description = description)
    class FINISHED(description: String = "FINISHED"): NfcAppMode(description = description)
}
