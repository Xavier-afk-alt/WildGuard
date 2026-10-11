package com.example.wildguard.page.explore

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wildguard.R
import com.example.wildguard.localization.AppLanguage

private val EncyclopediaGreen = Color(0xFF2B7147)

private data class WildlifeInfo(
    val name: String,
    val scientificName: String,
    val category: String,
    val status: String,
    val habitat: String,
    val diet: String,
    val behaviour: String,
    val description: String,
    @DrawableRes val image: Int
)

private val wildlifeRecords = listOf(
    WildlifeInfo("Malayan Tiger", "Panthera tigris jacksoni", "Mammal", "Critically Endangered", "Tropical forests of Peninsular Malaysia", "Deer, wild boar and small mammals", "Solitary and mostly active at night", "The Malayan tiger is an important forest predator. Keep a safe distance and never approach or feed one.", R.drawable.tiger_animal),
    WildlifeInfo("Asian Elephant", "Elephas maximus", "Mammal", "Endangered", "Lowland forests and grasslands", "Grass, bark, roots, fruit and leaves", "Social; females live in family herds", "Asian elephants use forest corridors to move between feeding areas. Roads through these corridors can create dangerous crossings.", R.drawable.home_elephant),
    WildlifeInfo("Sun Bear", "Helarctos malayanus", "Mammal", "Vulnerable", "Tropical evergreen forests", "Fruit, insects, honey and small animals", "Usually solitary and an excellent climber", "The sun bear is the smallest bear species. Habitat loss and illegal wildlife trade are major threats.", R.drawable.bear_animal),
    WildlifeInfo("Red Fox", "Vulpes vulpes", "Mammal", "Least Concern", "Woodland, grassland and rural edges", "Small mammals, birds, insects and fruit", "Alert, adaptable and mostly active at dusk", "Foxes normally avoid people. Observe quietly and secure food waste so they do not become dependent on humans.", R.drawable.fox_animal),
    WildlifeInfo("Grey Wolf", "Canis lupus", "Mammal", "Least Concern", "Forests, mountains and grasslands", "Deer and other medium to large animals", "Lives and hunts in family packs", "Wolves communicate through scent, posture and vocal calls. They should always be viewed from a long distance.", R.drawable.wolf_animal),
    WildlifeInfo("Lion", "Panthera leo", "Mammal", "Vulnerable", "Savannah, grassland and open woodland", "Antelope, zebra and other mammals", "Social; lives in groups called prides", "Lions are powerful predators. Never leave a vehicle or safe shelter when lions are nearby.", R.drawable.lion_animal),
    WildlifeInfo("Giant Panda", "Ailuropoda melanoleuca", "Mammal", "Vulnerable", "Temperate mountain forests", "Primarily bamboo", "Mostly solitary and spends many hours feeding", "Giant pandas depend on healthy bamboo forests and protected habitat corridors.", R.drawable.panda_animal)
)

@Composable
fun EncyclopediaPage(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedAnimal by remember { mutableStateOf<WildlifeInfo?>(null) }
    val categories = listOf("All", "Mammal", "Endangered")
    val filtered = wildlifeRecords.filter {
        (selectedCategory == "All" || it.category == selectedCategory ||
            (selectedCategory == "Endangered" && it.status in listOf("Vulnerable", "Endangered", "Critically Endangered"))) &&
            (query.isBlank() || it.name.contains(query, true) || it.scientificName.contains(query, true))
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF6FAF7))) {
        EncyclopediaHeader(navController)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            placeholder = { Text(AppLanguage.text("Search animal name", "搜索动物名称", "Cari nama haiwan")) },
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EncyclopediaGreen)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Surface(
                    color = if (selectedCategory == category) EncyclopediaGreen else Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        when (category) { "All" -> AppLanguage.text("All", "全部", "Semua"); "Mammal" -> AppLanguage.text("Mammal", "哺乳动物", "Mamalia"); else -> AppLanguage.text("Endangered", "濒危", "Terancam") },
                        color = if (selectedCategory == category) Color.White else EncyclopediaGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Text(
            AppLanguage.text("${filtered.size} wildlife records", "${filtered.size} 条野生动物记录", "${filtered.size} rekod hidupan liar"), color = Color(0xFF60766B), fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { animal -> WildlifeCard(animal) { selectedAnimal = animal } }
        }
    }

    selectedAnimal?.let { AnimalDetailsDialog(it) { selectedAnimal = null } }
}

@Composable
private fun EncyclopediaHeader(navController: NavController) {
    Column(Modifier.fillMaxWidth().background(EncyclopediaGreen).statusBarsPadding().padding(bottom = 13.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White)
            }
            Text(AppLanguage.text("Wildlife Encyclopedia", "野生动物百科", "Ensiklopedia Hidupan Liar"), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        }
        Text(AppLanguage.text("Learn about species and their behaviour", "了解物种及其行为", "Ketahui spesies dan tingkah lakunya"), color = Color.White.copy(alpha = .85f), fontSize = 12.sp, modifier = Modifier.padding(start = 52.dp))
    }
}

@Composable
private fun WildlifeCard(animal: WildlifeInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(animal.image), animal.name,
                modifier = Modifier.size(82.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(start = 13.dp).weight(1f)) {
                Text(localizedWildlifeText(animal.name), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Text(animal.scientificName, fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(7.dp))
                Surface(color = statusColor(animal.status), shape = RoundedCornerShape(12.dp)) {
                    Text(localizedWildlifeText(animal.status), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text(AppLanguage.text("Tap to view full information", "点击查看完整资料", "Tekan untuk maklumat penuh"), fontSize = 10.sp, color = EncyclopediaGreen, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Composable
private fun AnimalDetailsDialog(animal: WildlifeInfo, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedWildlifeText(animal.name), fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                Image(
                    painterResource(animal.image), animal.name,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(animal.scientificName, color = EncyclopediaGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 9.dp))
                DetailLine(AppLanguage.text("Conservation status", "保护状态", "Status pemuliharaan"), localizedWildlifeText(animal.status))
                DetailLine(AppLanguage.text("Habitat", "栖息地", "Habitat"), localizedWildlifeText(animal.habitat))
                DetailLine(AppLanguage.text("Diet", "食物", "Pemakanan"), localizedWildlifeText(animal.diet))
                DetailLine(AppLanguage.text("Behaviour", "行为", "Tingkah laku"), localizedWildlifeText(animal.behaviour))
                Text(localizedWildlifeText(animal.description), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(AppLanguage.text("Close", "关闭", "Tutup")) } }
    )
}

private fun localizedWildlifeText(value: String): String {
    val chinese = mapOf(
        "Malayan Tiger" to "马来亚虎", "Asian Elephant" to "亚洲象", "Sun Bear" to "马来熊",
        "Red Fox" to "赤狐", "Grey Wolf" to "灰狼", "Lion" to "狮子", "Giant Panda" to "大熊猫",
        "Mammal" to "哺乳动物", "Critically Endangered" to "极度濒危", "Endangered" to "濒危",
        "Vulnerable" to "易危", "Least Concern" to "无危",
        "Tropical forests of Peninsular Malaysia" to "马来西亚半岛热带森林",
        "Lowland forests and grasslands" to "低地森林与草原", "Tropical evergreen forests" to "热带常绿森林",
        "Woodland, grassland and rural edges" to "林地、草原及乡村边缘", "Forests, mountains and grasslands" to "森林、山区与草原",
        "Savannah, grassland and open woodland" to "稀树草原、草地与疏林", "Temperate mountain forests" to "温带山地森林",
        "Deer, wild boar and small mammals" to "鹿、野猪及小型哺乳动物", "Grass, bark, roots, fruit and leaves" to "草、树皮、根、果实与树叶",
        "Fruit, insects, honey and small animals" to "水果、昆虫、蜂蜜及小动物", "Small mammals, birds, insects and fruit" to "小型哺乳动物、鸟类、昆虫及水果",
        "Deer and other medium to large animals" to "鹿及其他中大型动物", "Antelope, zebra and other mammals" to "羚羊、斑马及其他哺乳动物", "Primarily bamboo" to "主要以竹子为食",
        "Solitary and mostly active at night" to "独居，主要在夜间活动", "Social; females live in family herds" to "群居；雌象组成家庭象群",
        "Usually solitary and an excellent climber" to "通常独居，擅长攀爬", "Alert, adaptable and mostly active at dusk" to "警觉且适应力强，多在黄昏活动",
        "Lives and hunts in family packs" to "以家族群体生活和狩猎", "Social; lives in groups called prides" to "群居，生活在狮群中", "Mostly solitary and spends many hours feeding" to "大多独居，每天长时间进食",
        "The Malayan tiger is an important forest predator. Keep a safe distance and never approach or feed one." to "马来亚虎是重要的森林捕食者。请保持安全距离，切勿靠近或喂食。",
        "Asian elephants use forest corridors to move between feeding areas. Roads through these corridors can create dangerous crossings." to "亚洲象会利用森林走廊往返觅食区，穿越走廊的道路可能形成危险路段。",
        "The sun bear is the smallest bear species. Habitat loss and illegal wildlife trade are major threats." to "马来熊是体型最小的熊类，栖息地流失和非法野生动物贸易是主要威胁。",
        "Foxes normally avoid people. Observe quietly and secure food waste so they do not become dependent on humans." to "狐狸通常会避开人类。请安静观察并妥善处理食物垃圾。",
        "Wolves communicate through scent, posture and vocal calls. They should always be viewed from a long distance." to "狼通过气味、姿势和叫声沟通，应始终从远处观察。",
        "Lions are powerful predators. Never leave a vehicle or safe shelter when lions are nearby." to "狮子是强大的捕食者。当附近有狮子时，切勿离开车辆或安全庇护所。",
        "Giant pandas depend on healthy bamboo forests and protected habitat corridors." to "大熊猫依赖健康的竹林和受保护的栖息地走廊。"
    )
    val malay = mapOf(
        "Malayan Tiger" to "Harimau Malaya", "Asian Elephant" to "Gajah Asia", "Sun Bear" to "Beruang Matahari",
        "Red Fox" to "Rubah Merah", "Grey Wolf" to "Serigala Kelabu", "Lion" to "Singa", "Giant Panda" to "Panda Gergasi",
        "Mammal" to "Mamalia", "Critically Endangered" to "Sangat Terancam", "Endangered" to "Terancam",
        "Vulnerable" to "Terdedah", "Least Concern" to "Kurang Membimbangkan",
        "Tropical forests of Peninsular Malaysia" to "Hutan tropika Semenanjung Malaysia",
        "Lowland forests and grasslands" to "Hutan tanah rendah dan padang rumput", "Tropical evergreen forests" to "Hutan malar hijau tropika",
        "Woodland, grassland and rural edges" to "Hutan, padang rumput dan pinggir desa", "Forests, mountains and grasslands" to "Hutan, gunung dan padang rumput",
        "Savannah, grassland and open woodland" to "Savana, padang rumput dan hutan terbuka", "Temperate mountain forests" to "Hutan gunung sederhana",
        "Deer, wild boar and small mammals" to "Rusa, babi hutan dan mamalia kecil", "Grass, bark, roots, fruit and leaves" to "Rumput, kulit kayu, akar, buah dan daun",
        "Fruit, insects, honey and small animals" to "Buah, serangga, madu dan haiwan kecil", "Small mammals, birds, insects and fruit" to "Mamalia kecil, burung, serangga dan buah",
        "Deer and other medium to large animals" to "Rusa dan haiwan sederhana hingga besar", "Antelope, zebra and other mammals" to "Antelop, zebra dan mamalia lain", "Primarily bamboo" to "Terutamanya buluh",
        "Solitary and mostly active at night" to "Bersendirian dan kebanyakannya aktif pada waktu malam", "Social; females live in family herds" to "Bersosial; betina hidup dalam kawanan keluarga",
        "Usually solitary and an excellent climber" to "Biasanya bersendirian dan pandai memanjat", "Alert, adaptable and mostly active at dusk" to "Peka, mudah menyesuaikan diri dan aktif ketika senja",
        "Lives and hunts in family packs" to "Hidup dan memburu dalam kumpulan keluarga", "Social; lives in groups called prides" to "Bersosial; hidup dalam kumpulan", "Mostly solitary and spends many hours feeding" to "Kebanyakannya bersendirian dan lama mencari makan",
        "The Malayan tiger is an important forest predator. Keep a safe distance and never approach or feed one." to "Harimau Malaya ialah pemangsa hutan yang penting. Kekalkan jarak selamat dan jangan dekati atau memberinya makan.",
        "Asian elephants use forest corridors to move between feeding areas. Roads through these corridors can create dangerous crossings." to "Gajah Asia menggunakan koridor hutan untuk bergerak antara kawasan pemakanan. Jalan di koridor ini boleh mewujudkan lintasan berbahaya.",
        "The sun bear is the smallest bear species. Habitat loss and illegal wildlife trade are major threats." to "Beruang matahari ialah spesies beruang terkecil. Kehilangan habitat dan perdagangan haram ialah ancaman utama.",
        "Foxes normally avoid people. Observe quietly and secure food waste so they do not become dependent on humans." to "Rubah biasanya mengelakkan manusia. Perhatikan dengan senyap dan simpan sisa makanan dengan selamat.",
        "Wolves communicate through scent, posture and vocal calls. They should always be viewed from a long distance." to "Serigala berkomunikasi melalui bau, postur dan bunyi. Perhatikan dari jarak jauh.",
        "Lions are powerful predators. Never leave a vehicle or safe shelter when lions are nearby." to "Singa ialah pemangsa yang kuat. Jangan keluar dari kenderaan atau tempat perlindungan apabila singa berdekatan.",
        "Giant pandas depend on healthy bamboo forests and protected habitat corridors." to "Panda gergasi bergantung pada hutan buluh yang sihat dan koridor habitat yang dilindungi."
    )
    return when (AppLanguage.current) {
        "中文" -> chinese[value] ?: value
        "Bahasa Melayu" -> malay[value] ?: value
        else -> value
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Text(label, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 7.dp))
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}

private fun statusColor(status: String) = when (status) {
    "Critically Endangered" -> Color(0xFFFFB5B5)
    "Endangered" -> Color(0xFFFFD0A8)
    "Vulnerable" -> Color(0xFFFFE9A8)
    else -> Color(0xFFCDEFD2)
}
