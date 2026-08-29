package com.example.wildguard.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wildguard.R
import com.example.wildguard.components.AchievementBanner
import com.example.wildguard.components.AnimalContent
import com.example.wildguard.components.InteractionBubble
import com.example.wildguard.components.InventoryItemDialog
import com.example.wildguard.components.InventoryQuickAccess
import com.example.wildguard.components.PlantContent
import com.example.wildguard.components.PointEarningPopup
import com.example.wildguard.components.RedeemButton
import com.example.wildguard.components.RewardHeader
import com.example.wildguard.components.RewardToggleButton
import com.example.wildguard.data.InventoryItem
import com.example.wildguard.data.animalList
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.viewmodel.RewardType
import com.example.wildguard.viewmodel.RewardViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


private fun canQuickUseItem(
    item: InventoryItem,
    rewardType: RewardType
): Boolean {

    return when (item.rewardId) {

        1 ->
            rewardType == RewardType.ANIMAL

        2 ->
            rewardType == RewardType.PLANT

        else ->
            false
    }
}


@Composable
fun RewardPage(

    navController: NavController,

    rewardViewModel: RewardViewModel,

    inventoryViewModel: InventoryViewModel

) {

    // ============================================================
    // POINT EVENT
    // ============================================================

    val firstPendingEvent =
        rewardViewModel
            .pendingPointEvents
            .firstOrNull()


    var visibleEventId by remember {
        mutableStateOf<String?>(null)
    }


    // ============================================================
    // INVENTORY
    // ============================================================

    var showInventory by remember {
        mutableStateOf(false)
    }


    var selectedInventoryItem by remember {
        mutableStateOf<InventoryItem?>(null)
    }


    // ============================================================
    // ANIMAL SELECTOR
    //
    // Needed so the long-press guide will not show while
    // the animal selector is expanded.
    // ============================================================

    var animalSelectorExpanded by remember {
        mutableStateOf(false)
    }


    // ============================================================
    // LONG-PRESS GUIDE STATE
    // ============================================================

    var showSceneGestureHint by remember {
        mutableStateOf(false)
    }


    var animalSceneHintShown by rememberSaveable {
        mutableStateOf(false)
    }


    var plantSceneHintShown by rememberSaveable {
        mutableStateOf(false)
    }


    // Increment whenever the user taps:
    //
    // ☝ Hold scene
    //
    // This will replay the full instruction.
    var sceneHintReplayRequest by rememberSaveable {
        mutableStateOf(0)
    }


    var lastHandledSceneHintReplayRequest by rememberSaveable {
        mutableStateOf(0)
    }


    // ============================================================
    // SCENE QUICK-USE PERMISSION
    //
    // Disabled when:
    //
    // - inventory is open
    // - inventory item detail dialog is open
    //
    // AnimalContent separately prevents quick-use while its
    // animal selector is expanded.
    // ============================================================

    val sceneQuickUseEnabled =

        !showInventory &&

                selectedInventoryItem == null


    // ============================================================
    // ACTUAL CLEAR SCENE FOR THE GUIDE
    // ============================================================

    val sceneIsClear =

        sceneQuickUseEnabled &&

                (
                        rewardViewModel.rewardType !=
                                RewardType.ANIMAL ||

                                !animalSelectorExpanded
                        )


    // ============================================================
    // ITEM REQUIRED BY CURRENT SCENE
    //
    // 1 = Pat Pat Hand
    // 2 = Watering Tool
    // ============================================================

    val sceneQuickUseItemId =

        when (
            rewardViewModel.rewardType
        ) {

            RewardType.ANIMAL ->
                1

            RewardType.PLANT ->
                2
        }


    // ============================================================
    // ONLY SHOW LONG-PRESS GUIDE IF USER OWNS THE RIGHT ITEM
    // ============================================================

    val sceneQuickUseItemAvailable =

        inventoryViewModel
            .items
            .any { item ->

                item.rewardId ==
                        sceneQuickUseItemId &&

                        item.quantity >
                        0
            }


    // ============================================================
    // TOOL NAME USED BY THE GUIDE
    // ============================================================

    val sceneGestureToolName =

        when (
            rewardViewModel.rewardType
        ) {

            RewardType.ANIMAL ->
                "Pat Pat Hand"

            RewardType.PLANT ->
                "Watering Tool"
        }


    // ============================================================
    // HAS USER ALREADY SEEN THE FULL GUIDE?
    // ============================================================

    val sceneHintHasBeenShown =

        when (
            rewardViewModel.rewardType
        ) {

            RewardType.ANIMAL ->
                animalSceneHintShown

            RewardType.PLANT ->
                plantSceneHintShown
        }


    // ============================================================
    // LONG-PRESS GUIDE TIMER
    //
    // First time:
    // full guide = 4 seconds
    //
    // Afterwards:
    // small permanent "☝ Hold scene"
    //
    // User can tap small reminder to replay full guide.
    // ============================================================

    LaunchedEffect(

        rewardViewModel.rewardType,

        sceneIsClear,

        sceneQuickUseItemAvailable,

        sceneHintReplayRequest

    ) {

        if (
            !sceneIsClear ||
            !sceneQuickUseItemAvailable
        ) {

            showSceneGestureHint =
                false

            return@LaunchedEffect
        }


        val alreadyShown =

            when (
                rewardViewModel.rewardType
            ) {

                RewardType.ANIMAL ->
                    animalSceneHintShown

                RewardType.PLANT ->
                    plantSceneHintShown
            }


        val replayRequested =

            sceneHintReplayRequest >
                    lastHandledSceneHintReplayRequest


        if (
            !alreadyShown ||
            replayRequested
        ) {

            if (replayRequested) {

                lastHandledSceneHintReplayRequest =
                    sceneHintReplayRequest
            }


            showSceneGestureHint =
                true


            delay(
                4000.milliseconds
            )


            showSceneGestureHint =
                false


            when (
                rewardViewModel.rewardType
            ) {

                RewardType.ANIMAL ->

                    animalSceneHintShown =
                        true


                RewardType.PLANT ->

                    plantSceneHintShown =
                        true
            }
        }
    }


    // ============================================================
    // POINT POPUP TIMER
    // ============================================================

    LaunchedEffect(
        firstPendingEvent?.id
    ) {

        if (
            firstPendingEvent ==
            null
        ) {

            visibleEventId =
                null

            return@LaunchedEffect
        }


        visibleEventId =
            firstPendingEvent.id


        delay(
            1800.milliseconds
        )


        visibleEventId =
            null


        delay(
            350.milliseconds
        )


        rewardViewModel
            .removePointEvent(
                firstPendingEvent.id
            )
    }


    // ============================================================
    // INTERACTION MESSAGE TIMER
    // ============================================================

    LaunchedEffect(

        rewardViewModel
            .showInteraction,

        rewardViewModel
            .interactionMessage

    ) {

        if (
            rewardViewModel
                .showInteraction
        ) {

            delay(
                3000.milliseconds
            )


            rewardViewModel
                .hideInteraction()
        }
    }


    // ============================================================
    // REACTION TIMER
    // ============================================================

    LaunchedEffect(
        rewardViewModel
            .reactionEventId
    ) {

        if (
            rewardViewModel
                .showReaction
        ) {

            delay(
                1600
            )


            rewardViewModel
                .hideReaction()
        }
    }


    // ============================================================
    // RESPONSIVE PAGE
    // ============================================================

    BoxWithConstraints(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(
                    0xFFF8F3D6
                )
            )

    ) {

        // ========================================================
        // GENERAL RESPONSIVE VALUES
        // ========================================================

        val narrowPhone =

            maxWidth <
                    360.dp


        val shortPhone =

            maxHeight <
                    620.dp


        val controlEdgePadding =

            if (narrowPhone) {

                10.dp

            } else {

                16.dp
            }


        val sceneTopGap =

            when {

                maxHeight <
                        560.dp ->

                    0.dp


                maxHeight <
                        650.dp ->

                    2.dp


                else ->

                    4.dp
            }


        val pointPopupTopPadding =

            if (shortPhone) {

                74.dp

            } else {

                88.dp
            }


        val interactionTopPadding =

            when (
                rewardViewModel
                    .rewardType
            ) {

                RewardType.ANIMAL ->
                    86.dp

                RewardType.PLANT ->
                    18.dp
            }


        val interactionMaxWidth =

            if (
                maxWidth >=
                360.dp
            ) {

                330.dp

            } else {

                maxWidth -
                        24.dp
            }


        // ========================================================
        // RESPONSIVE LONG-PRESS GUIDE
        //
        // IMPORTANT:
        //
        // Your real phone and emulator can have different:
        //
        // - physical resolution
        // - density
        // - display size
        // - Android font size
        //
        // So we check BOTH:
        //
        // maxWidth
        // LocalDensity.current.fontScale
        // ========================================================

        val currentFontScale =

            LocalDensity
                .current
                .fontScale


        // ========================================================
        // FULL GUIDE WIDTH
        //
        // Larger system font:
        // give the guide more screen width.
        //
        // Normal system font:
        // keep it smaller and less intrusive.
        // ========================================================

        val holdHintWidthFraction =

            when {

                currentFontScale >=
                        1.30f ->

                    0.90f


                currentFontScale >=
                        1.15f ->

                    0.86f


                maxWidth <
                        340.dp ->

                    0.88f


                maxWidth <
                        400.dp ->

                    0.82f


                else ->

                    0.74f
            }


        // ========================================================
        // FULL GUIDE TITLE FONT
        //
        // The value becomes slightly smaller when Android font
        // scaling is already large.
        // ========================================================

        val holdHintTitleFontSize =

            when {

                currentFontScale >=
                        1.30f ->

                    9.sp


                currentFontScale >=
                        1.15f ||

                        maxWidth <
                        360.dp ->

                    10.sp


                else ->

                    11.sp
            }


        // ========================================================
        // SECOND LINE FONT
        // ========================================================

        val holdHintSubtitleFontSize =

            when {

                currentFontScale >=
                        1.30f ->

                    8.sp


                currentFontScale >=
                        1.15f ||

                        maxWidth <
                        360.dp ->

                    9.sp


                else ->

                    10.sp
            }


        // ========================================================
        // HORIZONTAL PADDING
        // ========================================================

        val holdHintHorizontalPadding =

            if (
                maxWidth <
                360.dp ||

                currentFontScale >=
                1.15f
            ) {

                10.dp

            } else {

                12.dp
            }


        // ========================================================
        // VERTICAL PADDING
        // ========================================================

        val holdHintVerticalPadding =

            if (shortPhone) {

                6.dp

            } else {

                7.dp
            }


        // ========================================================
        // VERY IMPORTANT POSITION FIX
        //
        // RewardToggleButton is 72.dp high.
        //
        // Instead of:
        //
        // bottom = 74.dp
        //
        // we calculate its position using:
        //
        // edge padding + button height + extra spacing
        //
        // This prevents the hint from overlapping the large
        // Animals / Plants circle on the real phone.
        // ========================================================

        val holdHintBottomPadding =

            controlEdgePadding +

                    72.dp +

                    if (shortPhone) {

                        8.dp

                    } else {

                        12.dp
                    }


        // ========================================================
        // SMALL PERMANENT GUIDE FONT
        // ========================================================

        val smallHoldHintFontSize =

            when {

                currentFontScale >=
                        1.30f ->

                    8.sp


                currentFontScale >=
                        1.15f ||

                        maxWidth <
                        350.dp ->

                    9.sp


                else ->

                    10.sp
            }


        // ========================================================
        // INTERACTION ICON
        // ========================================================

        val interactionIconRes =

            when (
                rewardViewModel
                    .rewardType
            ) {

                RewardType.ANIMAL -> {

                    animalList
                        .firstOrNull {

                            it.type ==
                                    rewardViewModel
                                        .selectedAnimal
                        }
                        ?.icon

                        ?: R.drawable.animal
                }


                RewardType.PLANT -> {

                    R.drawable.plant
                }
            }


        // ========================================================
        // MAIN PAGE
        // ========================================================

        Column(

            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            // ====================================================
            // HEADER
            // ====================================================

            RewardHeader()


            // ====================================================
            // ACHIEVEMENT
            // ====================================================

            AchievementBanner(

                earnedPoints =
                    rewardViewModel
                        .totalEarnedPoints,

                maxPoint =
                    rewardViewModel
                        .maxPoint,

                status =
                    rewardViewModel
                        .status,

                borderColor =
                    rewardViewModel
                        .statusColor,

                onClick = {

                    navController
                        .navigate(
                            "achievements"
                        )
                }
            )


            // ====================================================
            // REDEEM
            // ====================================================

            RedeemButton(

                coin =
                    rewardViewModel
                        .currentPoint

            ) {

                navController
                    .navigate(
                        "redeem"
                    )
            }


            Spacer(

                modifier =
                    Modifier.height(
                        sceneTopGap
                    )
            )


            // ====================================================
            // REWARD SCENE
            // ====================================================

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        1f
                    )

            ) {

                // =================================================
                // PLANT / ANIMAL CONTENT
                // =================================================

                when (
                    rewardViewModel
                        .rewardType
                ) {

                    RewardType.PLANT -> {

                        PlantContent(

                            rewardViewModel =
                                rewardViewModel,

                            inventoryViewModel =
                                inventoryViewModel,

                            sceneQuickUseEnabled =
                                sceneQuickUseEnabled,

                            modifier =
                                Modifier.fillMaxSize()
                        )
                    }


                    RewardType.ANIMAL -> {

                        AnimalContent(

                            rewardViewModel =
                                rewardViewModel,

                            inventoryViewModel =
                                inventoryViewModel,

                            sceneQuickUseEnabled =
                                sceneQuickUseEnabled,

                            onSelectorExpandedChanged = {
                                    expanded ->

                                animalSelectorExpanded =
                                    expanded
                            },

                            modifier =
                                Modifier.fillMaxSize()
                        )
                    }
                }


                // =================================================
                // FULL LONG-PRESS GUIDE
                //
                // The message is deliberately divided into:
                //
                // LINE 1:
                // Hold scene: Watering Tool
                //
                // LINE 2:
                // Release to stop
                //
                // This prevents the real phone from making:
                //
                // Hold scene: Watering Tool • Release to
                // stop
                //
                // =================================================

                if (
                    showSceneGestureHint
                ) {

                    Surface(

                        modifier = Modifier
                            .align(
                                Alignment.BottomCenter
                            )

                            // Place it safely above the bottom
                            // Animals/Plants button.
                            .padding(
                                bottom =
                                    holdHintBottomPadding
                            )

                            // Responsive percentage width.
                            .fillMaxWidth(
                                holdHintWidthFraction
                            )

                            // Prevent excessive width on
                            // larger devices.
                            .widthIn(
                                max = 340.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        color =
                            Color.White.copy(
                                alpha = 0.88f
                            ),

                        shadowElevation =
                            3.dp

                    ) {

                        Column(

                            modifier =
                                Modifier.padding(

                                    horizontal =
                                        holdHintHorizontalPadding,

                                    vertical =
                                        holdHintVerticalPadding
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally

                        ) {

                            // =====================================
                            // LINE 1
                            // =====================================

                            Text(

                                text =
                                    "Hold scene: $sceneGestureToolName",

                                color =
                                    Color(
                                        0xFF40533B
                                    ),

                                fontSize =
                                    holdHintTitleFontSize,

                                lineHeight =
                                    13.sp,

                                fontWeight =
                                    FontWeight.Medium,

                                textAlign =
                                    TextAlign.Center,

                                maxLines =
                                    1
                            )


                            Spacer(

                                modifier =
                                    Modifier.height(
                                        1.dp
                                    )
                            )


                            // =====================================
                            // LINE 2
                            // =====================================

                            Text(

                                text =
                                    "Release to stop",

                                color =
                                    Color(
                                        0xFF52634C
                                    ).copy(
                                        alpha =
                                            0.84f
                                    ),

                                fontSize =
                                    holdHintSubtitleFontSize,

                                lineHeight =
                                    12.sp,

                                fontWeight =
                                    FontWeight.Normal,

                                textAlign =
                                    TextAlign.Center,

                                maxLines =
                                    1
                            )
                        }
                    }
                }


                // =================================================
                // SMALL PERMANENT LONG-PRESS REMINDER
                //
                // Appears after the full guide.
                //
                // Tapping it shows the full guide again.
                // =================================================

                if (
                    sceneIsClear &&

                    sceneQuickUseItemAvailable &&

                    sceneHintHasBeenShown &&

                    !showSceneGestureHint
                ) {

                    Surface(

                        modifier = Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                bottom =
                                    holdHintBottomPadding
                            )
                            .clickable {

                                sceneHintReplayRequest +=
                                    1
                            },

                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),

                        color =
                            Color.White.copy(
                                alpha = 0.72f
                            ),

                        shadowElevation =
                            1.dp

                    ) {

                        Text(

                            text =
                                "☝ Hold scene",

                            modifier =
                                Modifier.padding(
                                    horizontal =
                                        9.dp,
                                    vertical =
                                        4.dp
                                ),

                            color =
                                Color(
                                    0xFF52634C
                                ),

                            fontSize =
                                smallHoldHintFontSize,

                            lineHeight =
                                12.sp,

                            fontWeight =
                                FontWeight.Medium,

                            textAlign =
                                TextAlign.Center
                        )
                    }
                }


                // =================================================
                // NORMAL ANIMAL / PLANT MESSAGE
                // =================================================

                if (
                    rewardViewModel
                        .showInteraction &&

                    rewardViewModel
                        .interactionMessage
                        .isNotEmpty()
                ) {

                    Box(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start =
                                    12.dp,
                                top =
                                    interactionTopPadding,
                                end =
                                    12.dp
                            ),

                        contentAlignment =
                            Alignment.TopCenter

                    ) {

                        InteractionBubble(

                            text =
                                rewardViewModel
                                    .interactionMessage,

                            iconRes =
                                interactionIconRes,

                            modifier =
                                Modifier.widthIn(
                                    max =
                                        interactionMaxWidth
                                )
                        )
                    }
                }
            }
        }


        // ========================================================
        // INVENTORY QUICK ACCESS
        // ========================================================

        InventoryQuickAccess(

            inventoryViewModel =
                inventoryViewModel,

            currentRewardType =
                rewardViewModel
                    .rewardType,

            expanded =
                showInventory,


            onToggle = {

                showInventory =
                    !showInventory
            },


            onItemClick = {
                    item ->

                selectedInventoryItem =
                    item
            },


            modifier = Modifier
                .align(
                    Alignment.BottomStart
                )
                .padding(
                    start =
                        controlEdgePadding,
                    bottom =
                        controlEdgePadding
                ),


            onQuickUse = {
                    item ->

                val allowed =

                    canQuickUseItem(

                        item =
                            item,

                        rewardType =
                            rewardViewModel
                                .rewardType
                    )


                if (allowed) {

                    val success =

                        rewardViewModel
                            .useQuickAccessItem(
                                rewardId =
                                    item.rewardId
                            )


                    if (success) {

                        inventoryViewModel
                            .useItem(
                                item.rewardId
                            )
                    }
                }
            },


            canQuickUse = {
                    item ->

                canQuickUseItem(

                    item =
                        item,

                    rewardType =
                        rewardViewModel
                            .rewardType
                )
            }
        )


        // ========================================================
        // INVENTORY ITEM DETAILS
        // ========================================================

        selectedInventoryItem
            ?.let {
                    item ->

                val canUseSelectedItem =

                    canQuickUseItem(

                        item =
                            item,

                        rewardType =
                            rewardViewModel
                                .rewardType
                    )


                InventoryItemDialog(

                    item =
                        item,


                    onDismiss = {

                        selectedInventoryItem =
                            null
                    },


                    onQuickAccess =

                        if (
                            canUseSelectedItem
                        ) {

                            {

                                val success =

                                    rewardViewModel
                                        .useQuickAccessItem(
                                            rewardId =
                                                item.rewardId
                                        )


                                if (success) {

                                    inventoryViewModel
                                        .useItem(
                                            item.rewardId
                                        )


                                    selectedInventoryItem =
                                        null
                                }
                            }

                        } else {

                            null
                        }
                )
            }


        // ========================================================
        // ANIMAL / PLANT TOGGLE
        // ========================================================

        RewardToggleButton(

            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(
                    controlEdgePadding
                ),

            currentType =
                rewardViewModel
                    .rewardType

        ) {

            rewardViewModel
                .switchRewardType()
        }


        // ========================================================
        // POINT POPUP
        // ========================================================

        PointEarningPopup(

            event =
                firstPendingEvent,

            visible =
                visibleEventId ==
                        firstPendingEvent?.id,

            modifier = Modifier
                .align(
                    Alignment.TopCenter
                )
                .padding(
                    top =
                        pointPopupTopPadding
                )
        )
    }
}