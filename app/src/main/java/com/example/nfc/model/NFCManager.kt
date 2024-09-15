package com.example.nfc.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class NFCManager @Inject constructor(
    private val context: Context
) : BroadcastReceiver() {
    private val TAG: String = this.javaClass.name
    private val _nfcState: MutableStateFlow<NFCState> = MutableStateFlow(NFCState.NotSupported())
    val nfcState = _nfcState.asStateFlow()



    private fun detectNFCState() {
        var nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        if (nfcAdapter == null) {
            _nfcState.value = NFCState.NotSupported()
        } else {
            if (nfcAdapter.isEnabled()){
                _nfcState.value = NFCState.Enabled()
            } else {
                _nfcState.value = NFCState.Disabled()
            }
        }
    }

    private fun registerIntent() {
        val intentFilter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        context.registerReceiver(this, intentFilter)
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == intent?.action) {
            val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
            when (state) {
                NfcAdapter.STATE_ON -> {
                    _nfcState.value = NFCState.Enabled()
                    Log.d(TAG, "NFC Turned on")
                }
                NfcAdapter.STATE_OFF -> {
                    _nfcState.value = NFCState.Disabled()
                    Log.d(TAG, "NFC Turned off")
                }
            }
        }
    }

    fun unregisterReceiver(){
        Log.d(TAG, "unregisterReceiver: Unregistering Receiver")
        context.unregisterReceiver(this)
    }


    fun onEvent(event: Lifecycle.Event){
        when(event){
            Lifecycle.Event.ON_CREATE -> {
                detectNFCState()
                registerIntent()
            }
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {}
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {
                unregisterReceiver()
            }
            Lifecycle.Event.ON_ANY -> {}
        }
    }
}