package com.example.assignment.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.assignment.components.ProfileHeader
import com.example.assignment.components.ProfileMenuCard
import com.example.assignment.components.UserInfoCard
import com.example.assignment.data.profileMenus

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

    navController: NavController

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

    }

}