package dev.spravedlivo.flibuster

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NavigationDestinations {
    val route: String
}

object BookSearch : NavigationDestinations {
    override val route: String = "booksearch"
}

object Book : NavigationDestinations {
    override val route: String = "book"
    const val bookUrl = "bookUrl"

    val routeWithArgs = "${route}/b/{${bookUrl}}"
    val arguments = listOf(
        navArgument(bookUrl) { type = NavType.StringType }
    )
    fun buildRoute(url: String): String {
        return "$route$url"
    }
}