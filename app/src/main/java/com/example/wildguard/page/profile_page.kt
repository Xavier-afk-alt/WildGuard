package com.example.wildguard.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wildguard.components.ProfileHeader
import com.example.wildguard.components.ProfileMenuCard
import com.example.wildguard.components.UserInfoCard
import com.example.wildguard.data.profileMenus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
==recommendation for profile each submodule part==
My Activities: Show the user's submitted wildlife reports, emergency calls, and completed guidebook activities.
My Rewards: Display redeemed reward items, remaining points, and owned collectibles.
Achievements: Show badges earned for milestones such as reporting incidents, daily logins, or conservation participation.
Edit Profile: Allow updating profile picture, username, email, and password.
Settings: Include notification preferences, language selection, dark mode, and app information.
Help & Support: Provide FAQs, emergency contact information, feedback submission, and app version details.
*/
@Composable
fun ProfilePage(

    navController: NavController,
    onSignOut: () -> Unit = {}

){

    Column(

        modifier=Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

    ){

        ProfileHeader()

        UserInfoCard()

        profileMenus.forEach{

            ProfileMenuCard(

                title=it.title,

                description=it.description

            ){

                navController.navigate(it.route)

            }

        }

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F7A4D))
        ) {
            Text("Log Out")
        }

    }

}
