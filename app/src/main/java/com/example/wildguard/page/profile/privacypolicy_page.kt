package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.localization.AppLanguage

private data class PolicySection(val title: String, val body: String)

@Composable
fun PrivacyPolicyPage(navController: NavController) {

    val policySections = listOf(
        PolicySection(
            AppLanguage.text("What we collect", "我们收集的信息", "Apa yang kami kumpulkan"),
            AppLanguage.text(
                "WildGuard collects location data only when you choose to attach it to a report, along with basic usage analytics if you opt in.",
                "WildGuard 仅在您选择将位置信息附加到举报时收集该数据，若您选择开启，也会收集基本使用情况分析。",
                "WildGuard hanya mengumpul data lokasi apabila anda memilih untuk melampirkannya pada laporan, bersama analitik penggunaan asas jika anda memilih untuk menyertainya."
            )
        ),
        PolicySection(
            AppLanguage.text("How we use it", "我们如何使用信息", "Bagaimana kami menggunakannya"),
            AppLanguage.text(
                "Location data helps power nearby wildlife alerts and report accuracy. Usage analytics, when enabled, helps us improve app performance.",
                "位置信息有助于提供附近野生动物警报及提高举报准确性。使用情况分析（若开启）有助于我们改进应用性能。",
                "Data lokasi membantu menyediakan makluman hidupan liar berhampiran dan ketepatan laporan. Analitik penggunaan, apabila diaktifkan, membantu kami menambah baik prestasi aplikasi."
            )
        ),
        PolicySection(
            AppLanguage.text("Data storage", "数据存储", "Penyimpanan data"),
            AppLanguage.text(
                "Account and authentication data is securely stored using Supabase's encrypted infrastructure.",
                "账号与身份验证数据通过 Supabase 的加密基础设施安全存储。",
                "Data akaun dan pengesahan disimpan dengan selamat menggunakan infrastruktur terenkripsi Supabase."
            )
        ),
        PolicySection(
            AppLanguage.text("Your controls", "您的控制权", "Kawalan anda"),
            AppLanguage.text(
                "You can turn location sharing and usage analytics on or off anytime from Privacy & Security settings.",
                "您可以随时在隐私与安全设置中开启或关闭位置共享与使用情况分析。",
                "Anda boleh mematikan atau menghidupkan perkongsian lokasi dan analitik penggunaan bila-bila masa dari tetapan Privasi & Keselamatan."
            )
        )
    )

    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("Privacy Policy", "隐私政策", "Dasar Privasi")) { navController.popBackStack() }
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            policySections.forEach { section ->
                Text(
                    section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                Text(
                    section.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}