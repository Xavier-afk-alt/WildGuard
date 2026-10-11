package com.example.wildguard.auth

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AuthLinkAction { None, VerifyEmail, ResetPassword }

object AuthDeepLinkState {
    var action by mutableStateOf(AuthLinkAction.None)
        private set

    fun handle(uri: Uri?) {
        if (uri?.scheme != "wildguard" || uri.host != "auth") return
        action = when (uri.path) {
            "/reset" -> AuthLinkAction.ResetPassword
            // Verification deliberately stays in the browser. Only reset
            // password is accepted as an Android deep link.
            else -> AuthLinkAction.None
        }
    }

    fun consume() {
        action = AuthLinkAction.None
    }
}
