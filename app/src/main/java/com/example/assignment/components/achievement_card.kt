package com.example.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment.data.Achievement

@Composable
fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) Color.White else Color(0xFFF3F3F3)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (achievement.unlocked) achievement.icon else "🔒",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(52.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (achievement.unlocked) "Unlocked" else "Locked",
                    color = if (achievement.unlocked) Color(0xFF2E7D32) else Color.Gray,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "${achievement.progress}/${achievement.target} • +${achievement.rewardPoints} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = if (achievement.unlocked) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Lock
                },
                contentDescription = null,
                tint = if (achievement.unlocked) Color(0xFF2E7D32) else Color.Gray
            )
        }
    }
}
