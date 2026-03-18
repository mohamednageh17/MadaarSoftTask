package com.madaarsoft.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                onEditUserClicked = { user -> navController.navigate("${Routes.INPUT}?userId=${user.id}") },
            )
        }

        composable(
            route = "${Routes.INPUT}?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
        ) {
            InputScreen(
                onUserAdded = { navController.popBackStack() },
            )
        }
    }
}
