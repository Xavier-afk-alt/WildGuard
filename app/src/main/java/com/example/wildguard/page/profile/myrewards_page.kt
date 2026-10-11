package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.data.rewardItems
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.data.repository.RewardCatalogRepository

@Composable
fun MyRewardsPage(navController: NavController, inventoryViewModel: InventoryViewModel) {
    val vouchers = inventoryViewModel.items.mapNotNull { owned ->
        val reward = RewardCatalogRepository.rewards.firstOrNull { it.id == owned.rewardId }?.toRewardItem()
            ?: rewardItems.firstOrNull { it.id == owned.rewardId }
        reward?.takeIf { it.type.equals("Voucher", true) }?.let { it to owned.quantity }
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("My Vouchers", "我的礼券", "Baucar Saya")) { navController.popBackStack() }
        if (vouchers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CardGiftcard, null, tint = Color.Gray, modifier = Modifier.size(58.dp))
                    Text(AppLanguage.text("No vouchers purchased yet", "还没有购买任何礼券", "Belum ada baucar dibeli"), fontWeight = FontWeight.Bold)
                    Text(AppLanguage.text("Purchased vouchers will appear here.", "购买的礼券会显示在这里。", "Baucar yang dibeli akan dipaparkan di sini."), color = Color.Gray)
                }
            }
        } else LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(vouchers, key = { it.first.id }) { (reward, quantity) ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(reward.image), reward.title, tint = Color.Unspecified, modifier = Modifier.size(72.dp))
                        Column(Modifier.padding(start = 13.dp).weight(1f)) {
                            Text(reward.title, fontWeight = FontWeight.ExtraBold)
                            Text(reward.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(
                                "${AppLanguage.text("Quantity", "数量", "Kuantiti")}: $quantity",
                                color = Color(0xFF08743F),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}