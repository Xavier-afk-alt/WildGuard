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
                Text("Wildlife Safety Guide", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }
            Text("What to do when encountering wildlife", color = Color.White.copy(alpha = .85f), fontSize = 12.sp, modifier = Modifier.padding(start = 52.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(13.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1C7)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WarningAmber, null, tint = Color(0xFFE07A00), modifier = Modifier.size(34.dp))
                Text(
                    "General rule: stay calm, keep your distance, do not feed wildlife and always leave it an escape route.",
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        Text("Select the animal you encountered", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp))
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
                    Text(" Emergency SOS", fontWeight = FontWeight.ExtraBold)
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
                    Text(guide.animal, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text(guide.summary, fontSize = 11.sp, color = Color(0xFF536E60))
                }
                Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null, tint = SafetyGreen)
            }

            if (expanded) {
                HorizontalDivider(Modifier.padding(vertical = 11.dp))
                Text("DO", color = SafetyGreen, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                guide.doSteps.forEach { GuideStep("✓", it, SafetyGreen) }
                Text("DO NOT", color = Color(0xFFD32F2F), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, modifier = Modifier.padding(top = 9.dp))
                guide.avoidSteps.forEach { GuideStep("✕", it, Color(0xFFD32F2F)) }
            }
        }
    }
}

@Composable
private fun GuideStep(symbol: String, text: String, color: Color) {
    Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.Top) {
        Text(symbol, color = color, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(22.dp))
        Text(text, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}
