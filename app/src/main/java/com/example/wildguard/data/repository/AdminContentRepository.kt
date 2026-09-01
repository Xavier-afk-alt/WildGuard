package com.example.wildguard.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ManagedNews(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @SerialName("image_base64") val imageBase64: String? = null,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class ManagedAnnouncement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class ManagedUser(
    val id: String,
    @SerialName("full_name") val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    @SerialName("is_disabled") val isDisabled: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

object AdminContentRepository {
    val news = mutableStateListOf<ManagedNews>()
    val announcements = mutableStateListOf<ManagedAnnouncement>()
    val users = mutableStateListOf<ManagedUser>()

    suspend fun refreshNews(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val rows = SupabaseProvider.client.from("news").select().decodeList<ManagedNews>()
            .sortedByDescending { it.createdAt }
        news.clear(); news.addAll(rows); Unit
    }

    suspend fun saveNews(item: ManagedNews): Result<Unit> = runCatching {
        SupabaseProvider.client.from("news").upsert(item) { onConflict = "id" }
        refreshNews().getOrThrow()
    }

    suspend fun deleteNews(id: String): Result<Unit> = runCatching {
        SupabaseProvider.client.from("news").delete { filter { eq("id", id) } }
        news.removeAll { it.id == id }; Unit
    }

    suspend fun refreshAnnouncements(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val rows = SupabaseProvider.client.from("announcements").select()
            .decodeList<ManagedAnnouncement>().sortedByDescending { it.createdAt }
        announcements.clear(); announcements.addAll(rows); Unit
    }

    suspend fun saveAnnouncement(item: ManagedAnnouncement): Result<Unit> = runCatching {
        SupabaseProvider.client.from("announcements").upsert(item) { onConflict = "id" }
        refreshAnnouncements().getOrThrow()
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = runCatching {
        SupabaseProvider.client.from("announcements").delete { filter { eq("id", id) } }
        announcements.removeAll { it.id == id }; Unit
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
        SupabaseProvider.client.from("profiles").delete { filter { eq("id", id) } }
        users.removeAll { it.id == id }; Unit
    }

    suspend fun isUserDisabled(userId: String): Boolean = runCatching {
        SupabaseProvider.client.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeList<ManagedUser>().firstOrNull()?.isDisabled == true
    }.getOrDefault(false)
}
