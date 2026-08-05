package com.quangkhai.getgo_application.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quangkhai.getgo_application.data.local.PrefManager
import com.quangkhai.getgo_application.presentation.ui.auth.LoginScreen
import com.quangkhai.getgo_application.presentation.ui.auth.RegisterScreen
import com.quangkhai.getgo_application.presentation.ui.main.MainScreen
import com.quangkhai.getgo_application.presentation.ui.menu.MenuScreen
import com.quangkhai.getgo_application.presentation.ui.intro.IntroScreen
import com.quangkhai.getgo_application.presentation.ui.friend.FriendListScreen
import com.quangkhai.getgo_application.presentation.ui.setting.UserSettingScreen
import com.quangkhai.getgo_application.presentation.ui.split.BillGroupScreen
import com.quangkhai.getgo_application.presentation.ui.split.SplitBillScreen
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel

// maps each route to a screen; screens report via callbacks, the graph navigates
@Composable
fun GetGoNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val session = remember { PrefManager(context) }
    val userViewModel: UserViewModel = viewModel()

    // start on the map/menu if a user is remembered, otherwise ask them to log in
    val startDestination = remember {
        if (session.getUserId() != null) GetGoRoute.Splash.route else GetGoRoute.Login.route
    }

    // one place for the ViewModel's error / status toasts
    LaunchedEffect(Unit) {
        userViewModel.message.collect { text ->
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(GetGoRoute.Login.route) {
            val currentUser by userViewModel.currentUser.collectAsState()
            LaunchedEffect(currentUser) {
                val id = currentUser?.id
                if (id != null) {
                    session.saveUserId(id)
                    navController.navigate(GetGoRoute.Splash.route) {
                        popUpTo(GetGoRoute.Login.route) { inclusive = true }
                    }
                }
            }
            LoginScreen(
                onSubmit = { username, password -> userViewModel.login(username, password) },
                onGoRegister = { navController.navigate(GetGoRoute.Register.route) }
            )
        }

        composable(GetGoRoute.Register.route) {
            val currentUser by userViewModel.currentUser.collectAsState()
            LaunchedEffect(currentUser) {
                val id = currentUser?.id
                if (id != null) {
                    session.saveUserId(id)
                    navController.navigate(GetGoRoute.Splash.route) {
                        popUpTo(GetGoRoute.Login.route) { inclusive = true }
                    }
                }
            }
            RegisterScreen(
                onSubmit = { name, username, password -> userViewModel.register(name, username, password) },
                onGoLogin = { navController.navigate(GetGoRoute.Login.route) }
            )
        }

        composable(GetGoRoute.Splash.route) {
            // enrich the remembered/just-authenticated user before the menu shows
            LaunchedEffect(Unit) {
                session.getUserId()?.let { userViewModel.loadUser(it) }
            }
            IntroScreen(
                onDone = {
                    navController.navigate(GetGoRoute.Menu.route) {
                        popUpTo(GetGoRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(GetGoRoute.Menu.route) {
            MenuScreen(
                onLetsGetGo = { navController.navigate(GetGoRoute.Home.route) },
                onFriendList = { navController.navigate(GetGoRoute.FriendList.route) },
                onUserSetting = { navController.navigate(GetGoRoute.UserProfile.route) },
                onSplitBill = { navController.navigate(GetGoRoute.SplitBill.route) }
            )
        }

        composable(GetGoRoute.Home.route) {
            MainScreen(
                onHome = { navController.navigate(GetGoRoute.Menu.route) },
                userViewModel = userViewModel
            )
        }

        composable(GetGoRoute.FriendList.route) {
            FriendListScreen(
                onBack = { navController.popBackStack() },
                userViewModel = userViewModel
            )
        }

        composable(GetGoRoute.UserProfile.route) {
            UserSettingScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    userViewModel.logout()
                    session.clear()
                    navController.navigate(GetGoRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                userViewModel = userViewModel
            )
        }

        composable(GetGoRoute.SplitBill.route) {
            SplitBillScreen(
                onBack = { navController.popBackStack() },
                userViewModel = userViewModel,
                onOpenGroup = { groupId -> navController.navigate(GetGoRoute.BillGroup.of(groupId)) }
            )
        }

        composable(
            GetGoRoute.BillGroup.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "new"
            BillGroupScreen(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                userViewModel = userViewModel
            )
        }
    }
}
