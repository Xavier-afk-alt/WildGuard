package com.example.wildguard.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wildguard.R
import com.example.wildguard.page.EmergencyPage
import com.example.wildguard.page.RedeemShopPage
import com.example.wildguard.page.ReportPage
import com.example.wildguard.page.ReportSecondPage
import com.example.wildguard.page.RewardPage
import com.example.wildguard.viewmodel.RewardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wildguard.page.ExplorePage
import com.example.wildguard.page.HomePage
import com.example.wildguard.page.ProfilePage
import com.example.wildguard.page.explore.EncyclopediaPage
import com.example.wildguard.page.explore.SafetyGuidePage
import com.example.wildguard.page.explore.WildlifeMapPage
import com.example.wildguard.page.profile.AchievementsPage
import com.example.wildguard.page.profile.EditProfilePage
import com.example.wildguard.page.profile.HelpSupportPage
import com.example.wildguard.page.profile.MyActivitiesPage
import com.example.wildguard.page.profile.MyRewardsPage
import com.example.wildguard.page.profile.SettingsPage
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.components.AppLoadingScreen
import com.example.wildguard.viewmodel.AppStateViewModel
import kotlinx.coroutines.delay

//create navigation route
sealed class Page(val route: String) {
    object Home : Page("home")
    object Explore : Page("explore")
    object WildlifeMap : Page("wildlife_map")

    object Encyclopedia : Page("encyclopedia")

    object SafetyGuide : Page("safety_guide")
    object Report : Page("report")
    object ReportDetail : Page("report_detail/{type}")
    object Emergency : Page("emergency")
    object Reward : Page("reward")
    object Redeem : Page("redeem")
    object Profile : Page("profile")
    object Activities : Page("activities")

    object MyRewards : Page("my_rewards")

    object Achievements : Page("achievements")

    object EditProfile : Page("edit_profile")

    object Settings : Page("settings")

    object Help : Page("help")
}

//create bottom nav items
data class BottomNavItem(
    val title: String, val route: String, val icon: Int
)

val bottomItems = listOf(

    BottomNavItem(
        "Home", Page.Home.route, R.drawable.home
    ),

    BottomNavItem(
        "Explore", Page.Explore.route, R.drawable.explore
    ),

    BottomNavItem(
        "Report", Page.Report.route, R.drawable.report
    ),

    BottomNavItem(
        "Reward", Page.Reward.route, R.drawable.reward
    ),

    BottomNavItem(
        "Profile", Page.Profile.route, R.drawable.profile
    )
)

fun navigateToTopLevel(
    navController: NavController,
    route: String
) {
    navController.navigate(route) {

        popUpTo(
            navController.graph.startDestinationId
        ) {
            saveState = true
        }

        launchSingleTop = true

        restoreState = true
    }
}

//create bottom nav bar
@Composable
fun BottomBar(navController: NavHostController) {

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar {

        bottomItems.forEach { item ->

            NavigationBarItem(

                selected = currentRoute == item.route,

                onClick = {
                    navigateToTopLevel(
                        navController = navController,
                        route = item.route
                    )
                },

                icon = {
                    Icon(
                        painter = painterResource(id = item.icon), contentDescription = item.title
                    )
                },

                label = {
                    Text(item.title)
                })
        }
    }
}

//setup nav
@Composable
fun AppNavigation(navController: NavHostController) {

    val rewardViewModel: RewardViewModel = viewModel()
    val inventoryViewModel: InventoryViewModel = viewModel()

    // Treat each app launch as one login event for the achievement system.
    LaunchedEffect(Unit) {
        rewardViewModel.recordLogin()
    }

    NavHost(
        navController = navController, startDestination = Page.Home.route
    ) {

        composable(Page.Home.route) {
            HomePage(navController)
        }

        composable(Page.Explore.route) {
            ExplorePage(navController)
        }

        composable(Page.WildlifeMap.route) {
            WildlifeMapPage(navController)
        }

        composable(Page.Encyclopedia.route) {
            EncyclopediaPage(navController)
        }

        composable(Page.SafetyGuide.route) {
            SafetyGuidePage(navController)
        }

        composable(Page.Report.route) {
            ReportPage(navController)
        }

        composable(
            Page.ReportDetail.route
        ) { backStackEntry ->

            val type = backStackEntry.arguments?.getString("type") ?: ""

            ReportSecondPage(
                navController = navController, reportType = type, rewardViewModel = rewardViewModel
            )
        }

        composable(Page.Emergency.route) {
            EmergencyPage(navController)
        }

        composable(Page.Reward.route) {
            RewardPage(
                navController = navController,
                rewardViewModel = rewardViewModel,
                inventoryViewModel = inventoryViewModel

            )
        }

        composable(Page.Redeem.route) {
            RedeemShopPage(
                navController = navController,
                rewardViewModel = rewardViewModel,
                inventoryViewModel = inventoryViewModel

            )
        }

        composable(Page.Profile.route) {
            ProfilePage(navController)
        }

        composable(Page.Activities.route) {

            MyActivitiesPage(navController)

        }

        composable(Page.MyRewards.route) {

            MyRewardsPage(navController)

        }

        composable(Page.Achievements.route) {

            AchievementsPage(
                navController = navController, rewardViewModel = rewardViewModel
            )

        }

        composable(Page.EditProfile.route) {

            EditProfilePage(navController)

        }

        composable(Page.Settings.route) {

            SettingsPage(navController)

        }

        composable(Page.Help.route) {

            HelpSupportPage(navController)

        }
    }
}

//main Page
@Composable
fun MainPage() {

    val appStateViewModel: AppStateViewModel =
        viewModel()

    val navController =
        rememberNavController()

    // ==========================================
    // APP STARTUP LOADING
    // ==========================================

    LaunchedEffect(Unit) {

        delay(1500)

        appStateViewModel.finishLoading()
    }

    // ==========================================
    // LOADING SCREEN
    // ==========================================

    if (appStateViewModel.isLoading) {

        AppLoadingScreen()

    } else {

        // ==========================================
        // MAIN APPLICATION
        // ==========================================

        Scaffold(

            bottomBar = {
                BottomBar(navController)
            }

        ) { padding ->

            Box(
                modifier = Modifier.padding(padding)
            ) {

                AppNavigation(navController)

            }
        }
    }
}