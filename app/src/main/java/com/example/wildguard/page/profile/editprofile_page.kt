package com.example.wildguard.page.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.data.profile.UserProfileStore
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import com.example.wildguard.localization.AppLanguage
import kotlinx.coroutines.launch

private val EditGreen = Color(0xFF08743F)

@Composable
fun EditProfilePage(navController: NavController) {
    val context = LocalContext.current
    val profileRevision = UserProfileStore.revision
    val profile = remember(profileRevision) { UserProfileStore.loadCurrentProfile(context) }
    val user = remember { if (SupabaseProvider.isConfigured) SupabaseProvider.client.auth.currentUserOrNull() else null }
    var fullName by remember { mutableStateOf(profile.fullName) }
    var phone by remember { mutableStateOf(profile.phone) }
    var avatarUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { avatarUri = it }

    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("Profile Information", "个人资料", "Maklumat Profil")) { navController.popBackStack() }
        Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(104.dp).clip(CircleShape).background(Color(0xFFE2F1E6)).clickable { picker.launch("image/*") }, contentAlignment = Alignment.Center) {
                when {
                    avatarUri != null -> AsyncImage(avatarUri, "New profile photo", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    profile.avatarPath != null -> AsyncImage(File(profile.avatarPath), "Profile photo", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else -> Icon(Icons.Outlined.CameraAlt, "Choose photo", tint = EditGreen, modifier = Modifier.size(36.dp))
                }
            }
            Text(AppLanguage.text("Change profile photo", "更换个人头像", "Tukar foto profil"), color = EditGreen, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { picker.launch("image/*") }.padding(10.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(fullName, { fullName = it }, label = { Text(AppLanguage.text("Full name", "姓名", "Nama penuh")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(profile.email, {}, label = { Text(AppLanguage.text("Email address", "电子邮箱", "Alamat e-mel")) }, enabled = false, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(phone, { phone = it.filter(Char::isDigit).take(15) }, label = { Text(AppLanguage.text("Phone number", "电话号码", "Nombor telefon")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = Color.Red) }
                    Button(onClick = {
                        if (fullName.isBlank()) message = AppLanguage.text("Full name is required.", "必须填写姓名。", "Nama penuh diperlukan.")
                        else {
                            isSaving = true
                            scope.launch {
                                UserProfileStore.syncCurrentProfile(
                                    context, fullName.trim(), profile.email, phone, avatarUri
                                ).onSuccess {
                                    UserProfileStore.saveSignUpProfile(context, fullName.trim(), profile.email, avatarUri, phone)
                                    navController.popBackStack()
                                }.onFailure { error ->
                                    message = error.message ?: AppLanguage.text("Unable to save profile.", "无法保存个人资料。", "Tidak dapat menyimpan profil.")
                                }
                                isSaving = false
                            }
                        }
                    }, enabled = !isSaving, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = EditGreen)) {
                        Text(if (isSaving) AppLanguage.text("Saving…", "正在保存…", "Menyimpan…") else AppLanguage.text("Save Changes", "保存更改", "Simpan Perubahan"))
                    }
                }
            }
            Text(AppLanguage.text("Your email address is linked to your login account and cannot be changed here.", "电子邮箱已关联登录账号，无法在这里更改。", "Alamat e-mel anda dikaitkan dengan akaun log masuk anda dan tidak boleh diubah di sini."), color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(14.dp))
        }
    }
}
