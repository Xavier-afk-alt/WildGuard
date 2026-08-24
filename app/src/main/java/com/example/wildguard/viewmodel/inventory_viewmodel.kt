package com.example.wildguard.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.wildguard.data.InventoryItem
import com.example.wildguard.data.RewardItem

class InventoryViewModel : ViewModel() {

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

        return true

    }

}