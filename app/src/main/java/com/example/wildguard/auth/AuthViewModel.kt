package com.example.wildguard.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

enum class AuthStatus { Checking, Unauthenticated, Authenticated }

class AuthViewModel : ViewModel() {
    var status by mutableStateOf(AuthStatus.Checking)
        private set
    var isBusy by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    private val client
        get() = if (SupabaseProvider.isConfigured) SupabaseProvider.client else null

    init {
        val supabase = client
        if (supabase == null) {
            status = AuthStatus.Unauthenticated
            message = "Add SUPABASE_URL and SUPABASE_PUBLISHABLE_KEY to local.properties or gradle.properties first."
        } else {
            status = if (supabase.auth.currentSessionOrNull() != null) {
                AuthStatus.Authenticated
            } else {
                AuthStatus.Unauthenticated
            }
            viewModelScope.launch {
                supabase.auth.sessionStatus.collectLatest {
                    status = if (supabase.auth.currentSessionOrNull() != null) {
                        AuthStatus.Authenticated
                    } else {
                        AuthStatus.Unauthenticated
                    }
                }
            }
        }
    }

    fun clearMessage() { message = null }

    fun login(email: String, password: String) = runAuth {
        val cleanEmail = email.trim()
        require(cleanEmail.isNotBlank() && password.isNotBlank()) {
            "Please enter email and password."
        }
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }

        client!!.auth.signInWith(Email) {
            this.email = cleanEmail
            this.password = password
        }
        status = AuthStatus.Authenticated
    }

    fun signUp(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        onComplete: () -> Unit
    ) = runAuth {
        val cleanEmail = email.trim()
        require(fullName.isNotBlank()) { "Please enter your full name." }
        require(cleanEmail.isNotBlank()) { "Please enter your email." }
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }
        require(password.length >= 6) { "Password must contain at least 6 characters." }
        require(password == confirmPassword) { "Passwords do not match." }
        client!!.auth.signUpWith(Email) {
            this.email = cleanEmail
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName.trim())
                put("phone", phone.trim())
            }
        }
        if (client!!.auth.currentSessionOrNull() != null) {
            status = AuthStatus.Authenticated
        } else {
            message = "Account created. Check your email to confirm, then log in."
            onComplete()
        }
    }

    fun loginWithGoogle() = runAuth { client!!.auth.signInWith(Google) }

    fun loginWithFacebook() = runAuth { client!!.auth.signInWith(Facebook) }

    fun signOut() = runAuth {
        client!!.auth.signOut()
        status = AuthStatus.Unauthenticated
    }

    private fun runAuth(block: suspend () -> Unit) {
        if (client == null) {
            message = "Supabase is not configured yet. See SUPABASE_PROFILE_SETUP.md."
            return
        }
        viewModelScope.launch {
            isBusy = true
            message = null
            runCatching { block() }
                .onFailure { message = friendlyAuthMessage(it) }
            isBusy = false
        }
    }

    private fun friendlyAuthMessage(error: Throwable): String {
        val raw = error.message.orEmpty()

        // Only these messages are created by our own input validation.
        val safeValidationMessages = setOf(
            "Please enter email and password.",
            "Please enter a valid email address.",
            "Please enter your full name.",
            "Please enter your email.",
            "Password must contain at least 6 characters.",
            "Passwords do not match."
        )
        if (error is IllegalArgumentException && raw in safeValidationMessages) {
            return raw
        }

        val text = raw.lowercase()
        return when {
            "invalid login credentials" in text ||
                    "invalid_credentials" in text ->
                "Incorrect email or password."

            "email not confirmed" in text ||
                    "email_not_confirmed" in text ->
                "Please confirm your email before logging in."

            "user already registered" in text ||
                    "already registered" in text ||
                    "user_already_exists" in text ->
                "This email is already registered. Please log in instead."

            "unable to validate email" in text ||
                    "invalid email" in text ||
                    "email_address_invalid" in text ||
                    "invalid format" in text ->
                "Please enter a valid email address."

            "password should be at least" in text ||
                    "weak_password" in text ->
                "Password must contain at least 6 characters."

            "too many requests" in text ||
                    "rate limit" in text ||
                    "over_email_send_rate_limit" in text ->
                "Too many attempts. Please wait a moment and try again."

            "network" in text ||
                    "failed to connect" in text ||
                    "timeout" in text ||
                    "unresolvedaddress" in text ||
                    "socket" in text ->
                "Unable to connect. Please check your internet connection."

            else -> "Authentication failed. Please try again."
        }
    }
}
