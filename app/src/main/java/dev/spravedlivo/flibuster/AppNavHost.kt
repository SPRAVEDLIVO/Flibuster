package dev.spravedlivo.flibuster

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.spravedlivo.flibuster.extensions.navigateSingleTopTo
import dev.spravedlivo.flibuster.ui.book.BookScreen
import dev.spravedlivo.flibuster.ui.booksearch.BookSearchScreen
import dev.spravedlivo.flibuster.ui.settings.SettingsScreen
import dev.spravedlivo.flibuster.viewmodel.BookSearchScreenViewModel

@Composable
fun AppNavHost(context: Context, navHostController: NavHostController) {
    NavHost(navController = navHostController, BookSearch.route) {
        composable(BookSearch.route) {
            BookSearchScreen(context) {
                navHostController.navigateSingleTopTo(
                    Book.buildRoute(it.url),
                    stateSaving = false,
                    stateRestoring = false)
            }
        }
        composable(SettingsRoute.route) {
            SettingsScreen(context)
        }
        composable(Book.routeWithArgs, arguments = Book.arguments) { navBackStackEntry ->
            val url =
                navBackStackEntry.arguments?.getString(Book.bookUrl)
            if (url == null) {
                navHostController.navigateSingleTopTo(BookSearch.route)
                return@composable
            }
            BookScreen(context, url = url)
        }
    }
}