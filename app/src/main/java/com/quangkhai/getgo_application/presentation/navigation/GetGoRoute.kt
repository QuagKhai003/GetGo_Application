package com.quangkhai.getgo_application.presentation.navigation

// every screen the NavGraph can show, named in one place
sealed class GetGoRoute(val route: String) {
    object Login : GetGoRoute("login")
    object Register : GetGoRoute("register")
    object Splash : GetGoRoute("splash")       // intro screen
    object Menu : GetGoRoute("menu")           // main/home grid
    object Home : GetGoRoute("home")           // the map screen
    object UserProfile : GetGoRoute("userProfile")

    object FriendList: GetGoRoute("friendList")

    object SplitBill: GetGoRoute("splitbill")

    // one bill group; pass "new" to start a fresh group
    object BillGroup: GetGoRoute("billGroup/{groupId}") {
        fun of(groupId: String) = "billGroup/$groupId"
    }
}
