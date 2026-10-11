package com.example.wildguard.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildguard.data.InventoryItem
import com.example.wildguard.data.RewardItem
import com.example.wildguard.data.rewardItems
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.repository.SupabaseInventoryRepository
import com.example.wildguard.data.repository.RewardCatalogRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    private val repository by lazy { SupabaseInventoryRepository() }
    private var userId: String? = null

    fun initializeForCurrentUser() {
        if (!SupabaseProvider.isConfigured) return
        val currentUserId = SupabaseProvider.client.auth.currentUserOrNull()?.id.orEmpty()
        if (currentUserId.isBlank() || currentUserId == userId) return
        userId = currentUserId
        viewModelScope.launch {
            RewardCatalogRepository.refresh()
            val quantities = repository.getInventory(currentUserId).getOrDefault(emptyMap())
            _items.clear()
            quantities.filterValues { it > 0 }.forEach { (rewardId, quantity) ->
                val reward = RewardCatalogRepository.rewards.firstOrNull { it.id == rewardId }?.toRewardItem()
                    ?: rewardItems.firstOrNull { it.id == rewardId }
                reward?.let { item ->
                    _items.add(InventoryItem(item.id, item.title, item.description, item.image, quantity))
                }
            }
        }
    }

    private fun persist(rewardId: Int, quantity: Int) {
        val currentUserId = userId ?: return
        viewModelScope.launch { repository.setQuantity(currentUserId, rewardId, quantity) }
    }

    private val _items =
        mutableStateListOf<InventoryItem>()

    val items: List<InventoryItem>
        get() = _items


    fun addItem(
        reward: RewardItem,
        quantity: Int = 1
    ) {

        if (quantity <= 0) return

        val index =
            _items.indexOfFirst {

                it.rewardId == reward.id

            }

        if (index == -1) {

            _items.add(

                InventoryItem(

                    rewardId = reward.id,

                    title = reward.title,

                    description = reward.description,

                    image = reward.image,

                    quantity = quantity

                )

            )

        } else {

            val oldItem =
                _items[index]

            _items[index] =
                oldItem.copy(

                    quantity =
                        oldItem.quantity + quantity

                )

        }

        persist(reward.id, _items.first { it.rewardId == reward.id }.quantity)

    }


    fun useItem(
        rewardId: Int
    ): Boolean {

        val index =
            _items.indexOfFirst {

                it.rewardId == rewardId

            }

        if (index == -1) {
            return false
        }

        val item =
            _items[index]

        if (item.quantity <= 0) {
            return false
        }

        if (item.quantity == 1) {

            _items.removeAt(index)

        } else {

            _items[index] =
                item.copy(

                    quantity =
                        item.quantity - 1

                )

        }

        persist(rewardId, _items.firstOrNull { it.rewardId == rewardId }?.quantity ?: 0)

        return true

    }

}
