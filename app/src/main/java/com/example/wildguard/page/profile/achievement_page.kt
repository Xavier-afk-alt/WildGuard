package com.example.wildguard.page.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.AchievementCard
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.components.VerticalScrollIndicators
import com.example.wildguard.data.Achievement
import com.example.wildguard.data.localizedDescription
import com.example.wildguard.data.localizedTitle
import com.example.wildguard.data.localizedUnlockCondition
import com.example.wildguard.viewmodel.RewardViewModel
import com.example.wildguard.localization.AppLanguage

@Composable
fun AchievementsPage(
    navController: NavController,
    rewardViewModel: RewardViewModel
) {
    var selectedAchievement by remember {
        mutableStateOf<Achievement?>(null)
    }

    val achievementListState =
        rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        PageTopBar(
            title = AppLanguage.text("Achievements", "我的成就", "Pencapaian"),
            onBackClick = {
                navController.popBackStack()
            }
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {

            LazyColumn(
                state = achievementListState,

                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                item {
                    Text(
                        text = AppLanguage.text("Your progress", "您的进度", "Kemajuan Anda"),
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "${rewardViewModel.achievements.count { it.unlocked }} / " +
                                    "${rewardViewModel.achievements.size} " +
                                    AppLanguage.text("achievements unlocked", "个成就已解锁", "pencapaian dibuka")
                    )

                    Text(
                        text =
                            AppLanguage.text("Lifetime points earned: ", "累计获得积分：", "Jumlah mata diperoleh: ") +
                                    rewardViewModel.totalEarnedPoints,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )
                }

                items(
                    items =
                        rewardViewModel.achievements,

                    key = { achievement ->
                        achievement.id
                    }
                ) { achievement ->

                    AchievementCard(
                        achievement = achievement,
                        onClick = {
                            selectedAchievement =
                                achievement
                        }
                    )
                }
            }

            VerticalScrollIndicators(
                canScrollBackward =
                    achievementListState.canScrollBackward,

                canScrollForward =
                    achievementListState.canScrollForward,

                modifier = Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .padding(end = 8.dp)
            )
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
            Text(
                "${achievement.icon} ${achievement.localizedTitle()}"
            )
        },

        text = {
            Column {
                Text(
                    achievement.localizedDescription()
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        if (achievement.unlocked) {
                            AppLanguage.text("UNLOCKED", "已解锁", "DIBUKA")
                        } else {
                            AppLanguage.text("LOCKED", "未解锁", "DIKUNCI")
                        },

                    color =
                        if (achievement.unlocked) {
                            MaterialTheme
                                .colorScheme
                                .primary
                        } else {
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                        },

                    style =
                        MaterialTheme
                            .typography
                            .labelLarge
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    AppLanguage.text("Progress: ", "进度：", "Kemajuan: ") +
                            "${achievement.progress}/" +
                            achievement.target
                )

                Text(
                    AppLanguage.text("Unlock condition: ", "解锁条件：", "Syarat membuka: ") +
                            achievement.localizedUnlockCondition()
                )

                Text(
                    AppLanguage.text("Reward: +", "奖励：+", "Ganjaran: +") +
                            achievement.rewardPoints +
                            AppLanguage.text(" points", " 积分", " mata")
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(AppLanguage.text("Close", "关闭", "Tutup"))
            }
        }
    )
}
