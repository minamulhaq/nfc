package com.example.nfc.model

sealed class NFCState(val name: String) {

    class NotSupported(name: String = "NotSupported"): NFCState(name = name)
    class Enabled(name: String = "Enabled"): NFCState(name = name)
    class Disabled(name: String = "Disabled"): NFCState(name = name)
}