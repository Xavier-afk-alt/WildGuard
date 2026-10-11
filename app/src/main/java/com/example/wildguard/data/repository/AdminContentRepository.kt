package com.example.wildguard.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID

@Serializable
data class ManagedNews(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @SerialName("image_base64") val imageBase64: String? = null,
    @SerialName("created_at") val createdAt: String = Instant.now().toString()
)

@Serializable
data class ManagedAnnouncement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: String = Instant.now().toString()
)

@Serializable
data class ManagedUser(
    val id: String,
    @SerialName("full_name") val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    @SerialName("profile_picture_base64") val profilePictureBase64: String? = null,
    @SerialName("is_disabled") val isDisabled: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

object AdminContentRepository {
    private const val PREFS_NAME = "admin_published_content"
    private const val NEWS_KEY = "news"
    private const val ANNOUNCEMENTS_KEY = "announcements"
    private val json = Json { ignoreUnknownKeys = true }
    private var appContext: Context? = null

    val news = mutableStateListOf<ManagedNews>()
    val announcements = mutableStateListOf<ManagedAnnouncement>()
    val users = mutableStateListOf<ManagedUser>()

    fun initialize(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext
        val preferences = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.getString(NEWS_KEY, null)?.let { saved ->
            runCatching { json.decodeFromString<List<ManagedNews>>(saved) }
                .onSuccess { news.clear(); news.addAll(it.sortedByDescending(ManagedNews::createdAt)) }
        }
        preferences.getString(ANNOUNCEMENTS_KEY, null)?.let { saved ->
            runCatching { json.decodeFromString<List<ManagedAnnouncement>>(saved) }
                .onSuccess { announcements.clear(); announcements.addAll(it.sortedByDescending(ManagedAnnouncement::createdAt)) }
        }
    }

    suspend fun refreshNews(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val rows = SupabaseProvider.client.from("news").select().decodeList<ManagedNews>()
            .sortedByDescending { it.createdAt }
        if (rows.isNotEmpty() || news.isEmpty()) {
            news.clear(); news.addAll(rows); persistNews()
        }
        Unit
    }

    suspend fun saveNews(item: ManagedNews): Result<Unit> = runCatching {
        upsertNewsLocally(item)
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }
        SupabaseProvider.client.from("news").upsert(item) { onConflict = "id" }
    }

    suspend fun deleteNews(id: String): Result<Unit> = runCatching {
        SupabaseProvider.client.from("news").delete { filter { eq("id", id) } }
        news.removeAll { it.id == id }; persistNews(); Unit
    }

    suspend fun refreshAnnouncements(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val rows = SupabaseProvider.client.from("announcements").select()
            .decodeList<ManagedAnnouncement>().sortedByDescending { it.createdAt }
        if (rows.isNotEmpty() || announcements.isEmpty()) {
            announcements.clear(); announcements.addAll(rows); persistAnnouncements()
        }
        Unit
    }

    suspend fun saveAnnouncement(item: ManagedAnnouncement): Result<Unit> = runCatching {
        upsertAnnouncementLocally(item)
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }
        SupabaseProvider.client.from("announcements").upsert(item) { onConflict = "id" }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = runCatching {
        SupabaseProvider.client.from("announcements").delete { filter { eq("id", id) } }
        announcements.removeAll { it.id == id }; persistAnnouncements(); Unit
    }

    suspend fun refreshUsers(): Result<Unit> = runCatching {
        val rows = SupabaseProvider.client.from("profiles").select().decodeList<ManagedUser>()
            .filterNot { it.email.equals("admin@wildguard.app", ignoreCase = true) }
            .sortedBy { it.fullName.lowercase() }
        users.clear(); users.addAll(rows); Unit
    }

    suspend fun saveUser(user: ManagedUser): Result<Unit> = runCatching {
        SupabaseProvider.client.from("profiles").upsert(user) { onConflict = "id" }
        refreshUsers().getOrThrow()
    }

    suspend fun deleteUserProfile(id: String): Result<Unit> = runCatching {
        SupabaseProvider.client.postgrest.rpc(
            function = "delete_wildguard_user",
            parameters = buildJsonObject {
                // Keep the parameter name used by the function already installed
                // in existing WildGuard Supabase projects.
                put("p_user_id", id)
            }
        )
        // Read the database again so the UI only removes a user after the
        // database deletion really succeeded.
        refreshUsers().getOrThrow()
    }

    suspend fun isUserDisabled(userId: String): Boolean = runCatching {
        SupabaseProvider.client.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeList<ManagedUser>().firstOrNull()?.isDisabled == true
    }.getOrDefault(false)

    private fun upsertNewsLocally(item: ManagedNews) {
        val oldIndex = news.indexOfFirst { it.id == item.id }
        if (oldIndex >= 0) news[oldIndex] = item else news.add(0, item)
        news.sortByDescending { it.createdAt }
        persistNews()
    }

    private fun upsertAnnouncementLocally(item: ManagedAnnouncement) {
        val oldIndex = announcements.indexOfFirst { it.id == item.id }
        if (oldIndex >= 0) announcements[oldIndex] = item else announcements.add(0, item)
        announcements.sortByDescending { it.createdAt }
        persistAnnouncements()
    }

    private fun persistNews() {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()
            ?.putString(NEWS_KEY, json.encodeToString(news.toList()))?.apply()
    }

    private fun persistAnnouncements() {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()
            ?.putString(ANNOUNCEMENTS_KEY, json.encodeToString(announcements.toList()))?.apply()
    }
}
