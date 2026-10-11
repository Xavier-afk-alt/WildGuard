package com.example.wildguard.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.wildguard.page.RewardPage
import com.example.wildguard.viewmodel.RewardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wildguard.page.ExplorePage
import com.example.wildguard.page.HomePage
import com.example.wildguard.page.ProfilePage
import com.example.wildguard.page.AnnouncementPage
import com.example.wildguard.page.NewsPage
import com.example.wildguard.page.explore.EncyclopediaPage
import com.example.wildguard.page.explore.SafetyGuidePage
import com.example.wildguard.page.explore.WildlifeMapPage
import com.example.wildguard.page.profile.AchievementsPage
import com.example.wildguard.page.profile.EditProfilePage
import com.example.wildguard.page.profile.HelpSupportPage
import com.example.wildguard.page.profile.MyActivitiesPage
import com.example.wildguard.page.profile.MyRewardsPage
import com.example.wildguard.page.profile.SettingsPage
import com.example.wildguard.page.profile.PrivacySecurityPage
import com.example.wildguard.page.profile.AboutWildGuardPage
import com.example.wildguard.page.profile.TermsOfServicePage
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.components.AppLoadingScreen
import com.example.wildguard.viewmodel.AppStateViewModel
import kotlinx.coroutines.delay
import com.example.wildguard.auth.AuthStatus
import com.example.wildguard.auth.AuthViewModel
import com.example.wildguard.page.auth.LoginScreen
import com.example.wildguard.page.auth.SignUpScreen
import com.example.wildguard.page.auth.AdminLoginScreen
import com.example.wildguard.page.auth.RoleSelectionScreen
import com.example.wildguard.page.auth.ForgotPasswordScreen
import com.example.wildguard.page.auth.NewPasswordScreen
import com.example.wildguard.page.auth.VerificationEmailScreen
import com.example.wildguard.auth.AuthDeepLinkState
import com.example.wildguard.auth.AuthLinkAction
import com.example.wildguard.page.admin.AdminApp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.wildguard.localization.AppLanguage
import androidx.compose.ui.platform.LocalContext
import com.example.wildguard.data.profile.UserProfileStore
import com.example.wildguard.page.profile.ActiveSessionPage
import com.example.wildguard.page.profile.PrivacyPolicyPage

//create navigation route
sealed class Page(val route: String) {
    object Home : Page("home")
    object Explore : Page("explore")
    object WildlifeMap : Page("wildlife_map")

    object Encyclopedia : Page("encyclopedia")

    object SafetyGuide : Page("safety_guide")
    object Report : Page("report")
    object Emergency : Page("emergency")
    object Reward : Page("reward")
    object Redeem : Page("redeem")
    object Profile : Page("profile")
    object Activities : Page("activities")

    object MyRewards : Page("my_rewards")

    object Achievements : Page("achievements")

    object EditProfile : Page("edit_profile")

    object Settings : Page("settings")

    object AboutWildGuard : Page("about_wildguard")

    object TermsOfService : Page("terms_of_service")

    object PrivacySecurity : Page("privacy_security")
    object ActiveSession : Page("active_session")
    object PrivacyPolicy : Page("privacy_policy")

    object Help : Page("help")
    object Announcements : Page("announcements")
    object News : Page("news")
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

        popUpTo(navController.graph.startDestinationId) { saveState = false }

        launchSingleTop = true

        restoreState = false
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
                    Text(
                        when (item.route) {
                            Page.Home.route -> AppLanguage.text("Home", "主页", "Utama")
                            Page.Explore.route -> AppLanguage.text("Explore", "探索", "Terokai")
                            Page.Report.route -> AppLanguage.text("Report", "举报", "Lapor")
                            Page.Reward.route -> AppLanguage.text("Reward", "奖励", "Ganjaran")
                            else -> AppLanguage.text("Profile", "个人资料", "Profil")
                        }
                    )
                })
        }
    }
}

//setup nav
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isAdmin: Boolean,
    onSignOut: () -> Unit
) {

    val rewardViewModel: RewardViewModel = viewModel()
    val inventoryViewModel: InventoryViewModel = viewModel()

    // Treat each app launch as one login event for the achievement system.
    LaunchedEffect(authViewModel.status) {
        if (authViewModel.status == AuthStatus.Authenticated && !isAdmin) {
            rewardViewModel.initializeForCurrentUser()
            inventoryViewModel.initializeForCurrentUser()
        }
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

        composable(Page.News.route) {
            NewsPage(navController)
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
            ReportPage(
                navController = navController,
                rewardViewModel = rewardViewModel
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
            ProfilePage(
                navController = navController,
                isAdmin = isAdmin,
                onSignOut = {
                    navigateToTopLevel(navController, Page.Home.route)
                    onSignOut()
                }
            )
        }

        composable(Page.Activities.route) {

            MyActivitiesPage(navController)

        }

        composable(Page.MyRewards.route) {

            MyRewardsPage(navController, inventoryViewModel)

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

        composable(Page.AboutWildGuard.route) {
            AboutWildGuardPage(navController)
        }

        composable(Page.TermsOfService.route) {
            TermsOfServicePage(navController)
        }

        composable(Page.Help.route) {

            HelpSupportPage(navController)

        }

        composable(Page.PrivacySecurity.route) {

            PrivacySecurityPage(navController)

        }

        composable(Page.ActiveSession.route) {

            ActiveSessionPage(navController)

        }

        composable(Page.PrivacyPolicy.route) {

            PrivacyPolicyPage(navController)

        }

        composable(Page.Announcements.route) {
            AnnouncementPage(navController)
        }
    }
}

//main Page
@Composable
fun MainPage() {

    val context = LocalContext.current

    val appStateViewModel: AppStateViewModel =
        viewModel()

    val navController =
        rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    var showSignUp by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }
    var pendingVerificationEmail by remember { mutableStateOf<String?>(null) }
    var pendingVerificationPassword by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var adminAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(selectedRole, adminAuthenticated, authViewModel.status) {
        val activeRole = when {
            selectedRole == "admin" && adminAuthenticated -> "admin"
            selectedRole == "user" && authViewModel.status == AuthStatus.Authenticated -> "user"
            else -> null
        }
        context.getSharedPreferences("wildguard_proximity_runtime", android.content.Context.MODE_PRIVATE)
            .edit().apply {
                if (activeRole == null) remove("active_role") else putString("active_role", activeRole)
            }.apply()
    }

    LaunchedEffect(authViewModel.status) {
        if (authViewModel.status == AuthStatus.Authenticated && selectedRole != "admin") {
            showSignUp = false
            selectedRole = "user"
            // ------------------------
            // profile-load from Supabase
            // ------------------------
            UserProfileStore.refreshCurrentProfileFromSupabase(context)
        }
    }

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

    if (appStateViewModel.isLoading || authViewModel.status == AuthStatus.Checking) {

        AppLoadingScreen()

    } else if (AuthDeepLinkState.action == AuthLinkAction.ResetPassword) {

        NewPasswordScreen(
            authViewModel = authViewModel,
            onPasswordUpdated = {
                AuthDeepLinkState.consume()
                showForgotPassword = false
                showSignUp = false
                selectedRole = "user"
            }
        )

    } else if (AuthDeepLinkState.action == AuthLinkAction.VerifyEmail) {

        VerificationEmailScreen(
            email = pendingVerificationEmail.orEmpty(),
            authViewModel = authViewModel,
            verified = authViewModel.status == AuthStatus.Authenticated,
            onCheckVerification = {
                if (authViewModel.status == AuthStatus.Authenticated) {
                    AuthDeepLinkState.consume()
                } else {
                    authViewModel.confirmEmailVerification(
                        email = pendingVerificationEmail.orEmpty(),
                        password = pendingVerificationPassword.orEmpty(),
                        onVerified = { AuthDeepLinkState.consume() }
                    )
                }
            },
            onContinue = {
                AuthDeepLinkState.consume()
                pendingVerificationEmail = null
                pendingVerificationPassword = null
                selectedRole = "user"
            },
            onBackToLogin = {
                AuthDeepLinkState.consume()
                pendingVerificationEmail = null
                pendingVerificationPassword = null
                selectedRole = "user"
            }
        )

    } else if (pendingVerificationEmail != null) {

        VerificationEmailScreen(
            email = pendingVerificationEmail.orEmpty(),
            authViewModel = authViewModel,
            verified = false,
            onCheckVerification = {
                authViewModel.confirmEmailVerification(
                    email = pendingVerificationEmail.orEmpty(),
                    password = pendingVerificationPassword.orEmpty(),
                    onVerified = {
                        pendingVerificationEmail = null
                        pendingVerificationPassword = null
                        showSignUp = false
                        selectedRole = "user"
                    }
                )
            },
            onContinue = {},
            onBackToLogin = {
                pendingVerificationEmail = null
                pendingVerificationPassword = null
                showSignUp = false
            }
        )

    } else if (selectedRole == null) {

        RoleSelectionScreen(
            onUserSelected = { selectedRole = "user" },
            onAdminSelected = { selectedRole = "admin" }
        )

    } else if (selectedRole == "admin" && !adminAuthenticated) {

        AdminLoginScreen(
            authViewModel = authViewModel,
            onBack = {
                selectedRole = null
                adminAuthenticated = false
            },
            onLoginSuccess = { adminAuthenticated = true }
        )

    } else if (selectedRole == "admin" && adminAuthenticated) {

        AdminApp(
            onLogout = {
                authViewModel.signOut()
                adminAuthenticated = false
                selectedRole = null
            }
        )

    } else if (selectedRole == "user" && authViewModel.status != AuthStatus.Authenticated) {

        if (showForgotPassword) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBack = {
                    showForgotPassword = false
                    authViewModel.clearMessage()
                }
            )
        } else if (showSignUp) {
            SignUpScreen(
                authViewModel = authViewModel,
                onBackToLogin = { showSignUp = false },
                onVerificationSent = { email, password ->
                    pendingVerificationEmail = email
                    pendingVerificationPassword = password
                }
            )
        } else {
            LoginScreen(
                authViewModel = authViewModel,
                onOpenSignUp = { showSignUp = true },
                onForgotPassword = {
                    showForgotPassword = true
                    authViewModel.clearMessage()
                },
                onBack = {
                    showSignUp = false
                    selectedRole = null
                }
            )
        }

    } else {

        // ==========================================
        // MAIN APPLICATION
        // ==========================================

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val isWildlifeMap = currentBackStackEntry?.destination?.route == Page.WildlifeMap.route

        Scaffold(

            bottomBar = {
                BottomBar(navController)
            }

        ) { padding ->

            Box(
                modifier = Modifier.padding(
                    if (isWildlifeMap) {
                        // The map header draws behind and handles the status bar itself.
                        PaddingValues(bottom = padding.calculateBottomPadding())
                    } else {
                        padding
                    }
                )
            ) {

                AppNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    isAdmin = selectedRole == "admin",
                    onSignOut = {
                        authViewModel.signOut()
                        adminAuthenticated = false
                        showSignUp = false
                        selectedRole = null
                    }
                )

            }
        }
    }
}
