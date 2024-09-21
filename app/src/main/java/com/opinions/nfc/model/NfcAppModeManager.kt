package com.opinions.nfc.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NfcAppModeManager @Inject constructor() {
    private val _nfcAppMode: MutableStateFlow<NfcAppMode> = MutableStateFlow(NfcAppMode.READ())
    val nfcAppMode = _nfcAppMode.asStateFlow()



    fun updateNfcAppMode(mode: NfcAppMode) {
        _nfcAppMode.value = mode
    }
}