package com.example.wildguard.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AppStateViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    fun finishLoading() {
        isLoading = false
    }
}