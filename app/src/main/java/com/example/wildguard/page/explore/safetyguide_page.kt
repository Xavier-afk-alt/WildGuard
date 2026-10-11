package com.example.wildguard.page.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wildguard.navigation.Page
import com.example.wildguard.localization.AppLanguage

private val SafetyGreen = Color(0xFF2B7147)

private data class EncounterGuide(
    val animal: String,
    val icon: String,
    val summary: String,
    val doSteps: List<String>,
    val avoidSteps: List<String>
)

private val encounterGuides = listOf(
    EncounterGuide(
        "Elephant", "🐘", "Stay far away and never block its route.",
        listOf("Remain calm and move away slowly", "Keep at least 100 metres away", "Use a large tree or solid object as cover", "Allow the elephant a clear escape path"),
        listOf("Do not shout, use flash or sound a horn", "Do not stand between an adult and calf", "Do not run directly toward a vehicle through the herd")
    ),
    EncounterGuide(
        "Tiger or Big Cat", "🐅", "Face the animal and make yourself appear larger.",
        listOf("Stay calm and maintain visual contact", "Back away slowly without turning around", "Stand tall and speak firmly", "If attacked, fight back and protect your neck"),
        listOf("Do not run or crouch down", "Do not turn your back", "Do not approach cubs or a feeding animal")
    ),
    EncounterGuide(
        "Bear", "🐻", "Give the bear space and leave calmly.",
        listOf("Speak calmly so the bear identifies you", "Back away slowly", "Keep children close and make the group visible", "Use bear spray only if trained and threatened"),
        listOf("Do not run, scream or climb a tree", "Do not feed or approach the bear", "Do not place yourself near cubs")
    ),
    EncounterGuide(
        "Snake", "🐍", "Stop, locate the snake and move away slowly.",
        listOf("Keep several metres away", "Let the snake leave on its own", "If bitten, stay still and call emergency services", "Remove rings or tight items near the bite"),
        listOf("Do not touch, catch or kill it", "Do not cut or suck the wound", "Do not apply ice or a tight tourniquet")
    ),
    EncounterGuide(
        "Monkey", "🐒", "Avoid eye contact and secure food.",
        listOf("Move away calmly", "Keep bags closed and food hidden", "Give the monkey a clear route", "Wash scratches and seek medical care"),
        listOf("Do not smile, stare or show teeth", "Do not feed or tease it", "Do not pull an item directly from its hands")
    ),
    EncounterGuide(
        "Wild Boar", "🐗", "Create distance and place a barrier between you.",
        listOf("Back away slowly", "Move behind a tree, wall or vehicle", "Keep dogs restrained", "If knocked down, protect your head and neck"),
        listOf("Do not corner or chase it", "Do not approach piglets", "Do not run in a straight line across open ground")
    )
)

@Composable
fun SafetyGuidePage(navController: NavController) {
    var expandedAnimal by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(Color(0xFFF6FAF7))) {
        Column(Modifier.fillMaxWidth().background(SafetyGreen).statusBarsPadding().padding(bottom = 13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White)
                }
                Text(AppLanguage.text("Wildlife Safety Guide", "野生动物安全指南"), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }
            Text(AppLanguage.text("What to do when encountering wildlife", "遇到野生动物时该怎么做"), color = Color.White.copy(alpha = .85f), fontSize = 12.sp, modifier = Modifier.padding(start = 52.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(13.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1C7)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WarningAmber, null, tint = Color(0xFFE07A00), modifier = Modifier.size(34.dp))
                Text(
                    AppLanguage.text("General rule: stay calm, keep your distance, do not feed wildlife and always leave it an escape route.", "基本原则：保持冷静和距离，不要喂食，并始终为野生动物留出逃生路线。"),
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        Text(AppLanguage.text("Select the animal you encountered", "选择您遇到的动物"), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(encounterGuides) { guide ->
                EncounterGuideCard(
                    guide = guide,
                    expanded = expandedAnimal == guide.animal,
                    onClick = { expandedAnimal = if (expandedAnimal == guide.animal) null else guide.animal }
                )
            }
            item {
                Button(
                    onClick = { navController.navigate(Page.Emergency.route) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Phone, null)
                    Text(AppLanguage.text(" Emergency SOS", " 紧急求救 SOS"), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun EncounterGuideCard(guide: EncounterGuide, expanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).background(Color(0xFFDDF3E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(guide.icon, fontSize = 27.sp) }
                Column(Modifier.padding(start = 11.dp).weight(1f)) {
                    Text(localizedSafetyText(guide.animal), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text(localizedSafetyText(guide.summary), fontSize = 11.sp, color = Color(0xFF536E60))
                }
                Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null, tint = SafetyGreen)
            }

            if (expanded) {
                HorizontalDivider(Modifier.padding(vertical = 11.dp))
                Text(AppLanguage.text("DO", "应该做", "LAKUKAN"), color = SafetyGreen, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                guide.doSteps.forEach { GuideStep("✓", localizedSafetyText(it), SafetyGreen) }
                Text(AppLanguage.text("DO NOT", "不要做", "JANGAN"), color = Color(0xFFD32F2F), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, modifier = Modifier.padding(top = 9.dp))
                guide.avoidSteps.forEach { GuideStep("✕", localizedSafetyText(it), Color(0xFFD32F2F)) }
            }
        }
    }
}

private fun localizedSafetyText(value: String): String {
    val chinese = mapOf(
        "Elephant" to "大象", "Tiger or Big Cat" to "老虎或大型猫科动物", "Bear" to "熊", "Snake" to "蛇", "Monkey" to "猴子", "Wild Boar" to "野猪",
        "Stay far away and never block its route." to "保持远距离，切勿阻挡它的路线。", "Face the animal and make yourself appear larger." to "面对动物，让自己看起来更高大。",
        "Give the bear space and leave calmly." to "给熊足够空间并冷静离开。", "Stop, locate the snake and move away slowly." to "停下，确认蛇的位置并慢慢离开。",
        "Avoid eye contact and secure food." to "避免眼神接触并收好食物。", "Create distance and place a barrier between you." to "拉开距离，并在你与野猪之间设置障碍物。",
        "Remain calm and move away slowly" to "保持冷静并慢慢离开", "Keep at least 100 metres away" to "至少保持100米距离", "Use a large tree or solid object as cover" to "利用大树或坚固物体作为掩护", "Allow the elephant a clear escape path" to "为大象留出清晰的离开路线",
        "Do not shout, use flash or sound a horn" to "不要喊叫、使用闪光灯或鸣笛", "Do not stand between an adult and calf" to "不要站在成年象与幼象之间", "Do not run directly toward a vehicle through the herd" to "不要穿过象群直接跑向车辆",
        "Stay calm and maintain visual contact" to "保持冷静并留意动物", "Back away slowly without turning around" to "不要转身，慢慢后退", "Stand tall and speak firmly" to "站直并用坚定语气说话", "If attacked, fight back and protect your neck" to "若遭攻击，应反击并保护颈部",
        "Do not run or crouch down" to "不要奔跑或蹲下", "Do not turn your back" to "不要背对动物", "Do not approach cubs or a feeding animal" to "不要靠近幼崽或正在进食的动物",
        "Speak calmly so the bear identifies you" to "平静说话，让熊知道你是人", "Back away slowly" to "慢慢后退", "Keep children close and make the group visible" to "让儿童靠近并保持群体明显", "Use bear spray only if trained and threatened" to "仅在受过训练且受到威胁时使用防熊喷雾",
        "Do not run, scream or climb a tree" to "不要奔跑、尖叫或爬树", "Do not feed or approach the bear" to "不要喂食或接近熊", "Do not place yourself near cubs" to "不要靠近幼崽",
        "Keep several metres away" to "保持数米距离", "Let the snake leave on its own" to "让蛇自行离开", "If bitten, stay still and call emergency services" to "若被咬，保持不动并联系紧急服务", "Remove rings or tight items near the bite" to "取下咬伤处附近的戒指或紧身物品",
        "Do not touch, catch or kill it" to "不要触碰、捕捉或杀死它", "Do not cut or suck the wound" to "不要切开或吸吮伤口", "Do not apply ice or a tight tourniquet" to "不要冰敷或绑紧止血带",
        "Move away calmly" to "冷静离开", "Keep bags closed and food hidden" to "关好袋子并藏好食物", "Give the monkey a clear route" to "为猴子留出路线", "Wash scratches and seek medical care" to "清洗抓伤并就医",
        "Do not smile, stare or show teeth" to "不要微笑、盯视或露出牙齿", "Do not feed or tease it" to "不要喂食或挑逗它", "Do not pull an item directly from its hands" to "不要直接从它手中抢东西",
        "Move behind a tree, wall or vehicle" to "移动到树、墙或车辆后方", "Keep dogs restrained" to "控制好狗只", "If knocked down, protect your head and neck" to "若被撞倒，保护头部和颈部", "Do not corner or chase it" to "不要围堵或追赶", "Do not approach piglets" to "不要靠近幼猪", "Do not run in a straight line across open ground" to "不要在空旷地带直线奔跑"
    )
    val malay = mapOf(
        "Elephant" to "Gajah", "Tiger or Big Cat" to "Harimau atau Kucing Besar", "Bear" to "Beruang", "Snake" to "Ular", "Monkey" to "Monyet", "Wild Boar" to "Babi Hutan",
        "Stay far away and never block its route." to "Kekal jauh dan jangan halang laluannya.", "Face the animal and make yourself appear larger." to "Hadapi haiwan dan jadikan diri kelihatan lebih besar.",
        "Give the bear space and leave calmly." to "Beri ruang kepada beruang dan beredar dengan tenang.", "Stop, locate the snake and move away slowly." to "Berhenti, kenal pasti lokasi ular dan beredar perlahan-lahan.",
        "Avoid eye contact and secure food." to "Elakkan hubungan mata dan simpan makanan.", "Create distance and place a barrier between you." to "Jauhkan diri dan letakkan penghalang di antara anda.",
        "Remain calm and move away slowly" to "Kekal tenang dan beredar perlahan-lahan", "Keep at least 100 metres away" to "Kekalkan jarak sekurang-kurangnya 100 meter", "Use a large tree or solid object as cover" to "Gunakan pokok besar atau objek kukuh sebagai perlindungan", "Allow the elephant a clear escape path" to "Berikan gajah laluan keluar yang jelas",
        "Do not shout, use flash or sound a horn" to "Jangan menjerit, menggunakan denyar atau membunyikan hon", "Do not stand between an adult and calf" to "Jangan berdiri antara gajah dewasa dan anak", "Do not run directly toward a vehicle through the herd" to "Jangan berlari melalui kawanan menuju ke kenderaan",
        "Stay calm and maintain visual contact" to "Kekal tenang dan perhatikan haiwan", "Back away slowly without turning around" to "Undur perlahan-lahan tanpa berpaling", "Stand tall and speak firmly" to "Berdiri tegak dan bercakap dengan tegas", "If attacked, fight back and protect your neck" to "Jika diserang, lawan dan lindungi leher",
        "Do not run or crouch down" to "Jangan berlari atau mencangkung", "Do not turn your back" to "Jangan membelakangi haiwan", "Do not approach cubs or a feeding animal" to "Jangan dekati anak atau haiwan yang sedang makan",
        "Speak calmly so the bear identifies you" to "Bercakap dengan tenang supaya beruang mengenali anda", "Back away slowly" to "Undur perlahan-lahan", "Keep children close and make the group visible" to "Pastikan kanak-kanak dekat dan kumpulan mudah dilihat", "Use bear spray only if trained and threatened" to "Gunakan semburan beruang hanya jika terlatih dan terancam",
        "Do not run, scream or climb a tree" to "Jangan berlari, menjerit atau memanjat pokok", "Do not feed or approach the bear" to "Jangan beri makan atau dekati beruang", "Do not place yourself near cubs" to "Jangan berada berhampiran anak beruang",
        "Keep several metres away" to "Kekalkan jarak beberapa meter", "Let the snake leave on its own" to "Biarkan ular beredar sendiri", "If bitten, stay still and call emergency services" to "Jika dipatuk, jangan bergerak dan hubungi kecemasan", "Remove rings or tight items near the bite" to "Tanggalkan cincin atau barang ketat berhampiran gigitan",
        "Do not touch, catch or kill it" to "Jangan sentuh, tangkap atau bunuh", "Do not cut or suck the wound" to "Jangan potong atau hisap luka", "Do not apply ice or a tight tourniquet" to "Jangan gunakan ais atau ikatan ketat",
        "Move away calmly" to "Beredar dengan tenang", "Keep bags closed and food hidden" to "Pastikan beg tertutup dan makanan disembunyikan", "Give the monkey a clear route" to "Berikan monyet laluan yang jelas", "Wash scratches and seek medical care" to "Cuci calar dan dapatkan rawatan",
        "Do not smile, stare or show teeth" to "Jangan senyum, merenung atau menunjukkan gigi", "Do not feed or tease it" to "Jangan beri makan atau mengusik", "Do not pull an item directly from its hands" to "Jangan tarik barang daripada tangannya",
        "Move behind a tree, wall or vehicle" to "Bergerak ke belakang pokok, dinding atau kenderaan", "Keep dogs restrained" to "Pastikan anjing dikawal", "If knocked down, protect your head and neck" to "Jika terjatuh, lindungi kepala dan leher", "Do not corner or chase it" to "Jangan kepung atau kejar", "Do not approach piglets" to "Jangan dekati anak babi", "Do not run in a straight line across open ground" to "Jangan berlari lurus di kawasan terbuka"
    )
    return when (AppLanguage.current) {
        "中文" -> chinese[value] ?: value
        "Bahasa Melayu" -> malay[value] ?: value
        else -> value
    }
}

@Composable
private fun GuideStep(symbol: String, text: String, color: Color) {
    Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.Top) {
        Text(symbol, color = color, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(22.dp))
        Text(text, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}
