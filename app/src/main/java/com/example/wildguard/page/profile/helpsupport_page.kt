package com.example.wildguard.page.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.localization.AppLanguage

private val HelpGreen = Color(0xFF08743F)

@Composable
fun HelpSupportPage(navController: NavController) {
    val context = LocalContext.current
    var openQuestion by remember { mutableStateOf<Int?>(null) }
    val questions = listOf(
        AppLanguage.text("How do I submit a wildlife report?", "如何提交野生动物报告？", "Bagaimana saya menghantar laporan hidupan liar?") to
                AppLanguage.text("Open Report, enter the incident details, choose its location and submit it.", "打开举报页面，填写事件详情、选择地点并提交。", "Buka Laporan, masukkan butiran insiden, pilih lokasinya dan hantar."),
        AppLanguage.text("How are nearby alerts selected?", "如何选择附近警报？", "Bagaimana amaran berdekatan dipilih?") to
                AppLanguage.text("WildGuard uses active community reports and shows reports close to your area.", "WildGuard 会根据有效的社区报告，显示您附近的事件。", "WildGuard menggunakan laporan komuniti aktif dan memaparkan laporan berhampiran kawasan anda."),
        AppLanguage.text("How do I earn reward points?", "如何赚取奖励积分？", "Bagaimana saya memperoleh mata ganjaran?") to
                AppLanguage.text("Complete eligible activities and reports. Your balance appears on the Reward page.", "完成符合条件的活动和报告，积分会显示在奖励页面。", "Lengkapkan aktiviti dan laporan yang layak. Baki anda dipaparkan di halaman Ganjaran."),
        AppLanguage.text("What should I do in an emergency?", "紧急情况应该怎么做？", "Apakah yang perlu saya lakukan semasa kecemasan?") to
                AppLanguage.text("Open Emergency Response, use the SOS options and keep a safe distance from the animal.", "打开紧急响应并使用 SOS，同时与动物保持安全距离。", "Buka Tindak Balas Kecemasan, gunakan pilihan SOS dan kekalkan jarak selamat daripada haiwan tersebut.")
    )
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("Help & Support", "帮助与支持", "Bantuan & Sokongan")) { navController.popBackStack() }
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(AppLanguage.text("How can we help?", "我们可以如何帮助您？", "Bagaimana kami boleh membantu?"), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge) }
            items(questions.size) { index ->
                val question = questions[index]
                Card(onClick = { openQuestion = if (openQuestion == index) null else index }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(15.dp)) {
                        Row { Icon(Icons.Outlined.HelpOutline, null, tint = HelpGreen); Text(question.first, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp).weight(1f)); Icon(if (openQuestion == index) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null) }
                        if (openQuestion == index) Text(question.second, color = Color.DarkGray, modifier = Modifier.padding(top = 10.dp))
                    }
                }
            }
            item { Text(AppLanguage.text("CONTACT SUPPORT", "联系支持", "HUBUNGI SOKONGAN"), color = HelpGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp)) }
            item {
                Card(onClick = { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:wildguard3353@gmail.com?subject=WildGuard%20Support"))) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Row(Modifier.padding(16.dp)) { Icon(Icons.Outlined.Email, null, tint = HelpGreen); Column(Modifier.padding(start = 12.dp)) { Text(AppLanguage.text("Email us", "发送电子邮件给我们", "E-mel kami"), fontWeight = FontWeight.Bold); Text("wildguard3353@gmail.com", color = Color.Gray) } }
                }
            }
            item { Text(AppLanguage.text("Support hours: Monday–Friday, 9:00 AM–5:00 PM", "服务时间：星期一至星期五，上午9时至下午5时", "Waktu sokongan: Isnin–Jumaat, 9:00 pagi–5:00 petang"), color = Color.Gray, modifier = Modifier.padding(6.dp)) }
        }
    }
}