package com.madaarsoft.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.madaarsoft.app.presentation.input.InputScreen
import com.madaarsoft.app.presentation.userlist.UserListScreen

private object Routes {
    const val USER_LIST = "user_list"
    const val INPUT = "input"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.USER_LIST,
    ) {
        composable(Routes.USER_LIST) {
            UserListScreen(
                onAddUserClicked = { navController.navigate(Routes.INPUT) },
            )
        }

        composable(Routes.INPUT) {
            InputScreen(
                onUserAdded = { navController.popBackStack() },
            )
        }
    }
}
