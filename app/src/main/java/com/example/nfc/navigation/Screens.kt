package com.example.nfc.navigation

sealed class Screens(val route: String) {
    data object Read: Screens("Read")
    data object Write: Screens("Write")
}