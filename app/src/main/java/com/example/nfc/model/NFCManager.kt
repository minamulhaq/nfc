package com.example.nfc.model

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.util.Log
import com.example.nfc.MainActivity
import com.example.nfc.util.PendingIntent_Mutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class NFCManager @Inject constructor(
    private val context: Context
) : BroadcastReceiver() {
    private val TAG: String = "NFCManager"
    private val _nfcState: MutableStateFlow<NFCState> = MutableStateFlow(NFCState.NotSupported())
    val nfcState = _nfcState.asStateFlow()

    private lateinit var pendingIntent: PendingIntent
    var nfcAdapter: NfcAdapter? = null

    fun detectNFCState() {
        if (nfcAdapter == null) {
            _nfcState.value = NFCState.NotSupported()
        } else {
            if (nfcAdapter!!.isEnabled){
                _nfcState.value = NFCState.Enabled()
            } else {
                _nfcState.value = NFCState.Disabled()
            }
        }
    }

    fun registerNfcForegroundDispatch(activity: Activity) {
        Log.d(TAG, "registerNfcForegroundDispatch: registering for foreground dispatch")
        pendingIntent = PendingIntent.getActivity(
            activity,
            0,
            Intent(activity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent_Mutable
        )

    }

    fun registerNFCStateChanged() {
        val intentFilter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        context.registerReceiver(this, intentFilter)
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




    // Enable foreground dispatch to capture NFC intents when the app is in the foreground
    fun enableForegroundDispatch(activity: Activity) {
//        val intentFiltersArray = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
//        val techListsArray = arrayOf(arrayOf(Ndef::class.java.name))
        if (nfcAdapter == null){
            Log.d(TAG, "enableForegroundDispatch: nfc adapter is null")
        }
        if (pendingIntent == null) {
            Log.d(TAG, "enableForegroundDispatch: pending intent is null")
        }

        nfcAdapter!!.enableForegroundDispatch(activity, pendingIntent, null, null)
    }


    // Disable foreground dispatch when the app is not in the foreground
    fun disableForegroundDispatch(context: Activity) {
        nfcAdapter!!.disableForegroundDispatch(context)
    }

    fun getNfcAdapter(activity: Activity) {
        Log.d(TAG, "getNfcAdapter: Getting NFC Adapter")
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }
}