package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.localization.AppLanguage

private val InformationGreen = Color(0xFF08743F)
private val InformationBackground = Color(0xFFF5F7F5)

@Composable
fun AboutWildGuardPage(navController: NavController) {
    val sections = listOf(
        InformationSection(
            Icons.Outlined.Info,
            AppLanguage.text("Our Mission", "我们的使命", "Misi Kami"),
            AppLanguage.text(
                "WildGuard helps communities protect wildlife by making it easier to report incidents, discover nearby wildlife activity, learn safe responses and support conservation efforts.",
                "WildGuard 让用户可以更方便地举报野生动物事件、查看附近动态、学习安全应对方法，并参与野生动物保护。",
                "WildGuard membantu komuniti melindungi hidupan liar dengan memudahkan pelaporan insiden, penemuan aktiviti hidupan liar berdekatan, mempelajari tindak balas selamat dan menyokong usaha pemuliharaan."
            )
        ),
        InformationSection(
            Icons.Outlined.LocationOn,
            AppLanguage.text("Report Notifications", "报告通知", "Pemberitahuan Laporan"),
            AppLanguage.text(
                "When push notifications are enabled, WildGuard notifies you when a new wildlife report is submitted. Reports marked as resolved are not treated as active reports.",
                "开启推送通知后，每当有人提交新的野生动物报告，WildGuard 都会通知你。已解决的报告不会被视为有效报告。",
                "Apabila pemberitahuan diaktifkan, WildGuard memberitahu anda apabila laporan hidupan liar baharu dihantar. Laporan yang selesai tidak dianggap aktif."
            )
        ),
        InformationSection(
            Icons.Outlined.Security,
            AppLanguage.text("Safety First", "安全第一", "Keselamatan Diutamakan"),
            AppLanguage.text(
                "Keep a safe distance from wildlife and do not attempt to capture, feed or confront an animal. For immediate danger, contact the relevant emergency or wildlife authority.",
                "请与野生动物保持安全距离，不要尝试捕捉、喂食或对抗动物。如有即时危险，请联系相关紧急服务或野生动物管理部门。",
                "Kekalkan jarak selamat daripada hidupan liar dan jangan cuba menangkap, memberi makan atau berhadapan dengan haiwan. Untuk bahaya segera, hubungi pihak berkuasa kecemasan atau hidupan liar yang berkenaan."
            )
        )
    )
    InformationPage(
        title = AppLanguage.text("About WildGuard", "关于 WildGuard", "Tentang WildGuard"),
        intro = AppLanguage.text("WildGuard • Version 1.0", "WildGuard • 版本 1.0", "WildGuard • Versi 1.0"),
        sections = sections,
        onBack = navController::popBackStack
    )
}

@Composable
fun TermsOfServicePage(navController: NavController) {
    val sections = listOf(
        InformationSection(Icons.Outlined.Description, AppLanguage.text("1. Acceptance of Terms", "1. 接受条款", "1. Penerimaan Terma"), AppLanguage.text("By creating an account or using WildGuard, you agree to these Terms & Conditions. If you do not agree, please stop using the application.", "创建账号或使用 WildGuard 即表示你同意本条款与条件。如果你不同意，请停止使用本应用。", "Dengan mencipta akaun atau menggunakan WildGuard, anda bersetuju dengan Terma & Syarat ini. Jika tidak bersetuju, sila berhenti menggunakan aplikasi.")),
        InformationSection(Icons.Outlined.Security, AppLanguage.text("2. Responsible Use", "2. 负责任地使用", "2. Penggunaan Bertanggungjawab"), AppLanguage.text("You must provide truthful information, submit genuine wildlife reports and avoid abusive, illegal or misleading content. Do not use the app to harass others or interfere with wildlife.", "你必须提供真实资料并提交真实的野生动物报告。不得发布辱骂、违法或误导内容，也不得利用本应用骚扰他人或干扰野生动物。", "Anda mesti memberikan maklumat benar, menghantar laporan hidupan liar yang sah dan mengelakkan kandungan kesat, haram atau mengelirukan.")),
        InformationSection(Icons.Outlined.LocationOn, AppLanguage.text("3. Location and Reports", "3. 位置与报告", "3. Lokasi dan Laporan"), AppLanguage.text("Location data may be used to display maps, identify nearby reports and support incident response. Community reports may be incomplete or inaccurate, so always assess your surroundings and follow official instructions.", "位置资料可用于显示地图、寻找附近报告和协助处理事件。社区报告可能不完整或不准确，因此请观察周围环境并遵循官方指示。", "Data lokasi boleh digunakan untuk memaparkan peta, mengenal pasti laporan berdekatan dan menyokong tindak balas insiden. Sentiasa ikut arahan rasmi.")),
        InformationSection(Icons.Outlined.Security, AppLanguage.text("4. Privacy and Account Security", "4. 隐私与账号安全", "4. Privasi dan Keselamatan Akaun"), AppLanguage.text("You are responsible for protecting your account credentials. WildGuard handles profile, report and location information only for application features and safety-related purposes, subject to the app's privacy settings.", "你有责任保护账号登录资料。WildGuard 仅根据应用隐私设置，将个人资料、报告及位置资料用于应用功能和安全相关用途。", "Anda bertanggungjawab melindungi maklumat log masuk. WildGuard menggunakan maklumat profil, laporan dan lokasi hanya untuk fungsi aplikasi dan tujuan keselamatan.")),
        InformationSection(Icons.Outlined.Info, AppLanguage.text("5. Safety Disclaimer", "5. 安全免责声明", "5. Penafian Keselamatan"), AppLanguage.text("WildGuard is an information and reporting tool, not an emergency service. Alerts do not guarantee that an area is safe or that every wildlife incident has been reported. Contact emergency services when there is immediate danger.", "WildGuard 是资料与报告工具，并非紧急服务。警报不能保证某区域安全，也不能保证所有野生动物事件都已被报告。如有即时危险，请联系紧急服务。", "WildGuard ialah alat maklumat dan pelaporan, bukan perkhidmatan kecemasan. Hubungi perkhidmatan kecemasan jika terdapat bahaya segera.")),
        InformationSection(Icons.Outlined.Description, AppLanguage.text("6. Changes and Suspension", "6. 变更与暂停", "6. Perubahan dan Penggantungan"), AppLanguage.text("Features and these terms may be updated to improve safety or comply with requirements. Accounts that misuse the service may be restricted or removed.", "为了提升安全或符合相关要求，应用功能和本条款可能会更新。滥用服务的账号可能会被限制或删除。", "Ciri dan terma ini boleh dikemas kini. Akaun yang menyalahgunakan perkhidmatan boleh disekat atau dipadamkan."))
    )
    InformationPage(
        title = AppLanguage.text("Terms & Conditions", "条款与条件", "Terma & Syarat"),
        intro = AppLanguage.text("Effective date: 4 September 2026", "生效日期：2026年9月4日", "Tarikh berkuat kuasa: 4 September 2026"),
        sections = sections,
        onBack = navController::popBackStack
    )
}

private data class InformationSection(val icon: ImageVector, val title: String, val body: String)

@Composable
private fun InformationPage(title: String, intro: String, sections: List<InformationSection>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(InformationBackground)) {
        PageTopBar(title, onBack)
        LazyColumn(
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text(intro, color = InformationGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) }
            sections.forEach { section ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(section.icon, null, tint = InformationGreen)
                            Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 9.dp))
                            Text(section.body, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF555555), modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
