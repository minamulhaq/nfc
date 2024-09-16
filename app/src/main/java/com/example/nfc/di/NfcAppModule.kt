package com.example.nfc.di

import android.content.Context
import com.example.nfc.model.NFCManager
import com.example.nfc.util.NfcIntentParser
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
    fun provideNfcIntentParser(): NfcIntentParser = NfcIntentParser()

}