package com.example.assignment.page.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.components.AchievementCard
import com.example.assignment.components.PageTopBar
import com.example.assignment.data.Achievement
import com.example.assignment.viewmodel.RewardViewModel

@Composable
fun AchievementsPage(
    navController: NavController,
    rewardViewModel: RewardViewModel
) {
    var selectedAchievement by remember {
        mutableStateOf<Achievement?>(null)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PageTopBar(
            title = "Achievements",
            onBackClick = {
                navController.popBackStack()
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Your progress",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${rewardViewModel.achievements.count { it.unlocked }} / ${rewardViewModel.achievements.size} achievements unlocked"
                )

                Text(
                    text = "Lifetime points earned: ${rewardViewModel.totalEarnedPoints}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
            }

            items(
                items = rewardViewModel.achievements,
                key = { it.id }
            ) { achievement ->
                AchievementCard(
                    achievement = achievement,
                    onClick = {
                        selectedAchievement = achievement
                    }
                )
            }
        }
    }

    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = {
                selectedAchievement = null
            }
        )
    }
}

@Composable
private fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${achievement.icon} ${achievement.title}")
        },
        text = {
            Column {
                Text(achievement.description)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (achievement.unlocked) "UNLOCKED" else "LOCKED",
                    color = if (achievement.unlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(8.dp))
                Text("Progress: ${achievement.progress}/${achievement.target}")
                Text("Unlock condition: ${achievement.unlockCondition}")
                Text("Reward: +${achievement.rewardPoints} points")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
