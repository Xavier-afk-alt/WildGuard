package com.example.wildguard.data.profile

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.ByteArrayOutputStream
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class UserProfile(
    val fullName: String,
    val email: String,
    val avatarPath: String?,
    val phone: String = ""
)

object UserProfileStore {
    private const val PREFS = "wildguard_user_profiles"
    var revision by mutableIntStateOf(0)
        private set

    @Serializable
    private data class RemoteProfile(
        @SerialName("full_name") val fullName: String? = null,
        val email: String? = null,
        val phone: String? = null,
        @SerialName("profile_picture_base64") val profilePictureBase64: String? = null
    )

    fun saveSignUpProfile(context: Context, fullName: String, email: String, avatarUri: Uri?, phone: String = "") {
        val cleanEmail = email.trim().lowercase()
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val avatarPath = avatarUri?.let { copyAvatar(context, cleanEmail, it) }
        preferences.edit()
            .putString("last_email", cleanEmail)
            .putString("name_$cleanEmail", fullName.trim())
            .putString("phone_$cleanEmail", phone.trim())
            .apply {
                if (avatarPath != null) putString("avatar_$cleanEmail", avatarPath)
            }
            .apply()
        revision++
    }

    fun loadCurrentProfile(context: Context): UserProfile {
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val user = if (SupabaseProvider.isConfigured) {
            SupabaseProvider.client.auth.currentUserOrNull()
        } else null
        val email = user?.email?.lowercase()
            ?: preferences.getString("last_email", null)
            ?: "admin@wildguard.app"
        val metadataName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
        val storedName = preferences.getString("name_$email", null)
        val fullName = storedName ?: metadataName ?: if (email == "admin@wildguard.app") "Administrator" else "WildGuard User"
        val storedAvatar = preferences.getString("avatar_$email", null)
            ?.takeIf { File(it).exists() }
        val phone = preferences.getString("phone_$email", null)
            ?: user?.userMetadata?.get("phone")?.jsonPrimitive?.contentOrNull.orEmpty()
        return UserProfile(fullName, email, storedAvatar, phone)
    }

    suspend fun syncCurrentProfile(
        context: Context,
        fullName: String,
        email: String,
        phone: String,
        avatarUri: Uri?
    ): Result<Unit> = runCatching {
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }
        val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            ?: error("Please sign in again before saving your profile.")
        val avatarBase64 = avatarUri?.let { encodeAvatar(context, it) }
        SupabaseProvider.client.from("profiles").update({
            set("full_name", fullName.trim())
            set("email", email.trim().lowercase())
            set("phone", phone.trim())
            if (avatarBase64 != null) set("profile_picture_base64", avatarBase64)
        }) {
            filter { eq("id", userId) }
        }
        Unit
    }

    suspend fun syncStoredCurrentProfile(context: Context): Result<Unit> {
        val profile = loadCurrentProfile(context)
        return syncCurrentProfile(
            context = context,
            fullName = profile.fullName,
            email = profile.email,
            phone = profile.phone,
            avatarUri = profile.avatarPath?.let { Uri.fromFile(File(it)) }
        )
    }

    // ------------------------
    // profile-load from Supabase
    // ------------------------
    suspend fun refreshCurrentProfileFromSupabase(context: Context): Result<Unit> = runCatching {
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }
        val user = SupabaseProvider.client.auth.currentUserOrNull()
            ?: error("Please sign in again to load your profile.")
        val remote = SupabaseProvider.client.from("profiles").select {
            filter { eq("id", user.id) }
        }.decodeList<RemoteProfile>().firstOrNull() ?: return@runCatching

        val email = remote.email?.takeIf { it.isNotBlank() }
            ?: user.email.orEmpty().trim().lowercase()
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val avatarPath = remote.profilePictureBase64
            ?.takeIf { it.isNotBlank() }
            ?.let { saveBase64Avatar(context, email, it) }

        preferences.edit()
            .putString("last_email", email)
            .putString("name_$email", remote.fullName?.takeIf { it.isNotBlank() }
                ?: user.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
                ?: "WildGuard User")
            .putString("phone_$email", remote.phone.orEmpty())
            .apply {
                if (avatarPath != null) putString("avatar_$email", avatarPath)
            }
            .apply()
        revision++
    }

    private fun copyAvatar(context: Context, email: String, uri: Uri): String? = runCatching {
        val directory = File(context.filesDir, "profile_avatars").apply { mkdirs() }
        val destination = File(directory, "avatar_${email.hashCode()}.img")
        context.contentResolver.openInputStream(uri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        destination.absolutePath
    }.getOrNull()

    private fun encodeAvatar(context: Context, uri: Uri): String? = runCatching {
        val source = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            ?: return@runCatching null
        val maxSide = 480f
        val scale = minOf(1f, maxSide / maxOf(source.width, source.height).toFloat())
        val resized = if (scale < 1f) Bitmap.createScaledBitmap(
            source,
            (source.width * scale).toInt().coerceAtLeast(1),
            (source.height * scale).toInt().coerceAtLeast(1),
            true
        ) else source
        val output = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 75, output)
        Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }.getOrNull()

    private fun saveBase64Avatar(context: Context, email: String, encoded: String): String? = runCatching {
        val payload = encoded.substringAfter("base64,", encoded)
        val bytes = Base64.decode(payload, Base64.DEFAULT)
        val directory = File(context.filesDir, "profile_avatars").apply { mkdirs() }
        File(directory, "avatar_${email.hashCode()}.img").apply { writeBytes(bytes) }.absolutePath
    }.getOrNull()
}
