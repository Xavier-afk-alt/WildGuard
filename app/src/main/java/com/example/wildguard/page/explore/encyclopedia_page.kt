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
            placeholder = { Text("Search animal name") },
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
                        category,
                        color = if (selectedCategory == category) Color.White else EncyclopediaGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Text(
            "${filtered.size} wildlife records", color = Color(0xFF60766B), fontSize = 12.sp,
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
            Text("Wildlife Encyclopedia", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        }
        Text("Learn about species and their behaviour", color = Color.White.copy(alpha = .85f), fontSize = 12.sp, modifier = Modifier.padding(start = 52.dp))
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
                Text(animal.name, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Text(animal.scientificName, fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(7.dp))
                Surface(color = statusColor(animal.status), shape = RoundedCornerShape(12.dp)) {
                    Text(animal.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text("Tap to view full information", fontSize = 10.sp, color = EncyclopediaGreen, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Composable
private fun AnimalDetailsDialog(animal: WildlifeInfo, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(animal.name, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                Image(
                    painterResource(animal.image), animal.name,
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(animal.scientificName, color = EncyclopediaGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 9.dp))
                DetailLine("Conservation status", animal.status)
                DetailLine("Habitat", animal.habitat)
                DetailLine("Diet", animal.diet)
                DetailLine("Behaviour", animal.behaviour)
                Text(animal.description, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
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
