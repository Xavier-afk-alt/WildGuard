package com.example.wildguard.auth

import android.util.Patterns
import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.profile.UserProfileStore
import com.example.wildguard.data.repository.AdminContentRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.ByteArrayOutputStream

private const val VERIFY_BROWSER_URL = "https://xavier-afk-alt.github.io/wildguard-verified/"

enum class AuthStatus { Checking, Unauthenticated, Recovery, Authenticated }

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
                    status = if (status == AuthStatus.Recovery) {
                        AuthStatus.Recovery
                    } else if (supabase.auth.currentSessionOrNull() != null) {
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
        val userId = client!!.auth.currentUserOrNull()?.id.orEmpty()
        if (userId.isNotBlank() && AdminContentRepository.isUserDisabled(userId)) {
            client!!.auth.signOut()
            throw IllegalArgumentException("This account has been disabled by an administrator.")
        }
        status = AuthStatus.Authenticated
    }

    fun loginAdmin(
        username: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        message = null

        if (username.trim() == "admin" && password == "Admin@123") {
            status = AuthStatus.Authenticated
            onSuccess()
        } else {
            message = "Incorrect admin username or password."
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        avatarUri: Uri?,
        context: Context,
        onComplete: () -> Unit
    ) = runAuth {
        val cleanEmail = email.trim()
        val profilePictureBase64 = avatarUri?.let { profilePictureToBase64(context, it) }
        require(fullName.isNotBlank()) { "Please enter your full name." }
        require(cleanEmail.isNotBlank()) { "Please enter your email." }
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }
        require(password.length >= 6) { "Password must contain at least 6 characters." }
        require(password == confirmPassword) { "Passwords do not match." }
        // Do not deep-link back into Android. Supabase will open the project's
        // browser Site URL after confirming the email. The user then returns
        // to WildGuard and taps "I've verified".
        client!!.auth.signUpWith(Email, redirectUrl = VERIFY_BROWSER_URL) {
            this.email = cleanEmail
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName.trim())
                put("phone", phone.trim())
                profilePictureBase64?.let { put("profile_picture_base64", it) }
            }
        }
        UserProfileStore.saveSignUpProfile(context, fullName, cleanEmail, avatarUri, phone)
        if (client!!.auth.currentSessionOrNull() != null) {
            status = AuthStatus.Authenticated
        } else {
            message = null
            onComplete()
        }
    }

    fun resendVerification(email: String) = runAuth {
        val cleanEmail = email.trim()
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }
        client!!.auth.resendEmail(
            io.github.jan.supabase.auth.OtpType.Email.SIGNUP,
            cleanEmail
        )
        message = "Verification email sent again."
    }

    /**
     * The verification link may be opened in a browser before the user returns
     * to WildGuard. Signing in is the authoritative Supabase check: an
     * unconfirmed account is rejected with `email_not_confirmed`, while a
     * confirmed account receives a real session and can finish registration.
     */
    fun confirmEmailVerification(
        email: String,
        password: String,
        onVerified: () -> Unit
    ) = runAuth {
        val cleanEmail = email.trim()
        require(cleanEmail.isNotBlank() && password.isNotBlank()) {
            "Please return to registration and enter your details again."
        }
        client!!.auth.signInWith(Email) {
            this.email = cleanEmail
            this.password = password
        }
        require(client!!.auth.currentSessionOrNull() != null) {
            "Open the verification link to be verified."
        }
        status = AuthStatus.Authenticated
        message = null
        onVerified()
    }

    fun sendPasswordResetOtp(email: String, onSent: () -> Unit) = runAuth {
        val cleanEmail = email.trim()
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }
        // The helper RPC is optional. Older Supabase projects may not have it yet,
        // so a missing RPC must not block password recovery for a valid account.
        val emailExists = runCatching {
            client!!.postgrest.rpc(
                function = "wildguard_email_exists",
                parameters = buildJsonObject {
                    put("p_email", cleanEmail)
                }
            ).data.trim().toBooleanStrictOrNull()
        }.getOrNull()
        require(emailExists != false) {
            "This email is not registered. Please register first."
        }
        client!!.auth.resetPasswordForEmail(email = cleanEmail)
        message = null
        onSent()
    }

    fun verifyPasswordResetOtp(
        email: String,
        otp: String,
        onVerified: () -> Unit
    ) = runAuth {
        val cleanEmail = email.trim()
        val cleanOtp = otp.trim()
        require(Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            "Please enter a valid email address."
        }
        require(cleanOtp.length == 8 && cleanOtp.all(Char::isDigit)) {
            "Please enter the 8-digit OTP from your email."
        }
        client!!.auth.verifyEmailOtp(
            type = io.github.jan.supabase.auth.OtpType.Email.RECOVERY,
            email = cleanEmail,
            token = cleanOtp
        )
        require(client!!.auth.currentSessionOrNull() != null) {
            "The OTP is invalid or expired. Please request a new code."
        }
        // OTP verification creates a temporary session. Keep the UI on the
        // recovery flow until the user has chosen a new password.
        status = AuthStatus.Recovery
        message = null
        onVerified()
    }

    fun updatePassword(
        password: String,
        confirmPassword: String,
        onComplete: () -> Unit
    ) = runAuth {
        require(password.length >= 6) { "Password must contain at least 6 characters." }
        require(password == confirmPassword) { "Passwords do not match." }
        client!!.auth.updateUser { this.password = password }
        client!!.auth.signOut()
        status = AuthStatus.Unauthenticated
        message = null
        onComplete()
    }

    private fun profilePictureToBase64(context: Context, uri: Uri): String? = runCatching {
        val source = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: return@runCatching null
        val maxSide = 320f
        val ratio = minOf(1f, maxSide / maxOf(source.width, source.height).toFloat())
        val picture = if (ratio < 1f) {
            Bitmap.createScaledBitmap(source, (source.width * ratio).toInt(), (source.height * ratio).toInt(), true)
        } else source
        val output = ByteArrayOutputStream()
        picture.compress(Bitmap.CompressFormat.JPEG, 72, output)
        Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }.getOrNull()

    fun loginWithGoogle() = runAuth {
        withTimeout(30_000) {
            client!!.auth.signInWith(Google, redirectUrl = "wildguard://auth") {
                queryParams["prompt"] = "select_account"
            }
        }
    }


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
            "Passwords do not match.",
            "Incorrect admin username or password.",
            "This account has been disabled by an administrator.",
            "Please return to registration and enter your details again.",
            "Open the verification link to be verified.",
            "Please enter the 8-digit OTP from your email.",
            "The OTP is invalid or expired. Please request a new code.",
            "This email is not registered. Please register first."
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
                "Open the verification link to be verified."

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

            "otp" in text || "token" in text ->
                "The OTP is invalid or expired. Please request a new code."

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
