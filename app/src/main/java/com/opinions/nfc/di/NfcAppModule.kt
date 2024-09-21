package com.opinions.nfc.di

import android.content.Context
import com.opinions.nfc.model.NFCManager
import com.opinions.nfc.model.NfcAppModeManager
import com.opinions.nfc.util.NfcIntentParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NfcAppModule {


    @Provides
    @Singleton
    fun provideNfcManager(@ApplicationContext context: Context): NFCManager {
        return NFCManager(context = context)
    }


    @Provides
    @Singleton
    fun provideNfcIntentParser(nfcAppModeManager: NfcAppModeManager): NfcIntentParser {
        return NfcIntentParser(nfcAppModeManager)
    }

    @Provides
    @Singleton
    fun provideNfcAppModeManager(): NfcAppModeManager {
        return NfcAppModeManager()
    }


}