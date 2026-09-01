package com.example.wildguard.data.profile

import android.content.Context
import android.net.Uri
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

data class UserProfile(
    val fullName: String,
    val email: String,
    val avatarPath: String?
)

object UserProfileStore {
    private const val PREFS = "wildguard_user_profiles"

    fun saveSignUpProfile(context: Context, fullName: String, email: String, avatarUri: Uri?) {
        val cleanEmail = email.trim().lowercase()
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val avatarPath = avatarUri?.let { copyAvatar(context, cleanEmail, it) }
        preferences.edit()
            .putString("last_email", cleanEmail)
            .putString("name_$cleanEmail", fullName.trim())
            .apply {
                if (avatarPath != null) putString("avatar_$cleanEmail", avatarPath)
            }
            .apply()
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
        return UserProfile(fullName, email, storedAvatar)
    }

    private fun copyAvatar(context: Context, email: String, uri: Uri): String? = runCatching {
        val directory = File(context.filesDir, "profile_avatars").apply { mkdirs() }
        val destination = File(directory, "avatar_${email.hashCode()}.img")
        context.contentResolver.openInputStream(uri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        destination.absolutePath
    }.getOrNull()
}
