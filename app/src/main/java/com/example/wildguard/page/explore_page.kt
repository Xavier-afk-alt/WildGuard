package com.example.wildguard.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wildguard.R
import com.example.wildguard.navigation.Page

private val ExploreGreen = Color(0xFF2B7147)

@Composable
fun ExplorePage(navController: NavController) {
    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth().background(ExploreGreen).statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.wildguard_logo), "WildGuard",
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                    )
                    Text("WILDGUARD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Row {
                    /*IconButton(onClick = { navController.navigate(Page.Announcements.route) }) {
                        Icon(Icons.Outlined.Notifications, "Notifications", tint = Color.White)
                    }*/
                    IconButton(onClick = { navController.navigate(Page.Profile.route) }) {
                        Icon(Icons.Outlined.AccountCircle, "Profile", tint = Color.White)
                    }
                }
            }
            Text("Explore More", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text("Discover tools to protect wildlife", color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
        }

        Column(Modifier.padding(horizontal = 11.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExploreOption(
                "Wildlife Map", "Wildlife crossing hotspots & alerts", Color(0xFF9BFFA8)
            ) { navController.navigate(Page.WildlifeMap.route) }
            ExploreOption(
                "Wildlife Encyclopedia", "Learn about species & their behaviour", Color(0xFFA7D4A7)
            ) { navController.navigate(Page.Encyclopedia.route) }
            ExploreOption(
                "Wildlife Safety Guide", "What to do when encountering wildlife", Color(0xFF9CE7F0)
            ) { navController.navigate(Page.SafetyGuide.route) }
        }
    }
}

@Composable
private fun ExploreOption(title: String, description: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(11.dp))
            .background(color).clickable(onClick = onClick).padding(horizontal = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF102419))
            Text(description, fontSize = 10.sp, color = Color(0xFF243B2E))
        }
        Icon(Icons.Outlined.ArrowForward, null, tint = ExploreGreen, modifier = Modifier.size(18.dp))
    }
}