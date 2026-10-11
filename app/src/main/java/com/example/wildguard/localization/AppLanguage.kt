package com.example.wildguard.localization

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppLanguage {
    private const val PREFS = "wildguard_app_preferences"
    private const val LANGUAGE_KEY = "language"

    var current by mutableStateOf("English")
        private set

    fun initialize(context: Context) {
        current = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(LANGUAGE_KEY, "English") ?: "English"
    }

    fun set(context: Context, language: String) {
        current = language
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(LANGUAGE_KEY, language).apply()
    }

    fun text(english: String, chinese: String, malay: String? = null): String =
        when (current) {
            "中文" -> chinese
            "Bahasa Melayu" -> malay ?: malayText[english] ?: english
            else -> english
        }

    private val malayText = mapOf(
        "Home" to "Utama", "Explore" to "Terokai", "Report" to "Lapor", "Reward" to "Ganjaran", "Profile" to "Profil",
        "Welcome back" to "Selamat kembali", "Explore Now  >" to "Terokai Sekarang  >", "Quick Access" to "Akses Pantas", "News" to "Berita",
        "Wildlife Alert" to "Amaran Hidupan Liar", "No Wildlife Alert" to "Tiada Amaran Hidupan Liar",
        "No wildlife incident has been reported yet." to "Belum ada insiden hidupan liar dilaporkan.", "View Details >" to "Lihat Butiran >",
        "Wildlife Map" to "Peta Hidupan Liar", "Wildlife Encyclopedia" to "Ensiklopedia Hidupan Liar", "Wildlife Safety Guide" to "Panduan Keselamatan Hidupan Liar",
        "What to do when encountering wildlife" to "Apa yang perlu dilakukan apabila bertemu hidupan liar", "Select the animal you encountered" to "Pilih haiwan yang anda temui",
        "General rule: stay calm, keep your distance, do not feed wildlife and always leave it an escape route." to "Peraturan umum: kekal tenang, jaga jarak, jangan beri makan dan sentiasa berikan laluan keluar.", " Emergency SOS" to " SOS Kecemasan",
        "Wildlife\nMap" to "Peta\nHidupan Liar", "Wildlife\nEncyclopedia" to "Ensiklopedia\nHidupan Liar", "Wildlife\nSafety Guide" to "Panduan Keselamatan\nHidupan Liar",
        "View nearby wildlife reports" to "Lihat laporan hidupan liar berdekatan", "Report Location" to "Lokasi Laporan", "Community Alert" to "Amaran Komuniti",
        "Report Wildlife Incident" to "Lapor Insiden Hidupan Liar", "Detected / Observed Species" to "Spesies Dikesan / Dilihat", "Description" to "Penerangan",
        "Search location..." to "Cari lokasi...", "Use Current Location" to "Gunakan Lokasi Semasa", "Cancel" to "Batal", "Confirm" to "Sahkan", "Continue" to "Teruskan",
        "Submit Report" to "Hantar Laporan", "Report Submitted" to "Laporan Dihantar", "View Reward" to "Lihat Ganjaran", "Done" to "Selesai",
        "Emergency Response" to "Tindak Balas Kecemasan", "Need Immediate Assistance?" to "Perlukan Bantuan Segera?",
        "Press and hold SOS for 3 seconds to open the wildlife emergency hotline." to "Tekan dan tahan SOS selama 3 saat untuk membuka talian kecemasan hidupan liar.",
        "Achievements" to "Pencapaian", "Your progress" to "Kemajuan anda", "My Vouchers" to "Baucar Saya", "No vouchers purchased yet" to "Belum ada baucar dibeli",
        "Purchased vouchers will appear here." to "Baucar yang dibeli akan dipaparkan di sini.", "My Report History" to "Sejarah Laporan Saya", "No reports submitted" to "Tiada laporan dihantar",
        "Profile Information" to "Maklumat Profil", "Change profile photo" to "Tukar foto profil", "Full name" to "Nama penuh", "Email address" to "Alamat e-mel",
        "Phone number" to "Nombor telefon", "Save Changes" to "Simpan Perubahan", "Full name is required." to "Nama penuh diperlukan.",
        "Privacy & Security" to "Privasi & Keselamatan", "Password & account security" to "Keselamatan kata laluan & akaun", "Location sharing" to "Perkongsian lokasi",
        "Usage analytics" to "Analitik penggunaan", "Active session" to "Sesi aktif", "Privacy Policy" to "Dasar Privasi", "Password security" to "Keselamatan kata laluan",
        "This device is currently signed in" to "Peranti ini sedang log masuk", "Help improve WildGuard with anonymous usage data" to "Bantu tingkatkan WildGuard dengan data penggunaan tanpa nama",
        "Use your location for nearby wildlife reports" to "Gunakan lokasi anda untuk laporan hidupan liar berdekatan", "Learn how WildGuard protects your information" to "Ketahui cara WildGuard melindungi maklumat anda",
        "Help & Support" to "Bantuan & Sokongan", "How can we help?" to "Bagaimana kami boleh membantu?", "CONTACT SUPPORT" to "HUBUNGI SOKONGAN",
        "How do I submit a wildlife report?" to "Bagaimana saya menghantar laporan hidupan liar?", "How are nearby alerts selected?" to "Bagaimana amaran berdekatan dipilih?",
        "How do I earn reward points?" to "Bagaimana saya memperoleh mata ganjaran?", "What should I do in an emergency?" to "Apakah yang perlu saya lakukan semasa kecemasan?",
        "About WildGuard" to "Tentang WildGuard", "Our Mission" to "Misi Kami", "Nearby Wildlife Alerts" to "Amaran Hidupan Liar Berdekatan", "Safety First" to "Keselamatan Diutamakan",
        "WildGuard • Version 1.0" to "WildGuard • Versi 1.0", "View All" to "Lihat Semua", "REDEEM" to "TEBUS", "Animals" to "Haiwan", "Plants" to "Tumbuhan",
        "Wildlife crossing hotspots & alerts" to "Kawasan lintasan hidupan liar & amaran", "Discover tools to protect wildlife" to "Temui alat untuk melindungi hidupan liar",
        "distance unavailable" to "jarak tidak tersedia", "Close" to "Tutup", "OK" to "OK"
    )
}
