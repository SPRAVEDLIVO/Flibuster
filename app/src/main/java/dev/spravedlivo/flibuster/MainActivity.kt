package dev.spravedlivo.flibuster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.spravedlivo.flibuster.extensions.navigateSingleTopTo
import dev.spravedlivo.flibuster.ui.book.BookScreen
import dev.spravedlivo.flibuster.ui.booksearch.BookSearchScreen
import dev.spravedlivo.flibuster.ui.components.NavigationRow
import dev.spravedlivo.flibuster.ui.theme.FlibusterTheme
import dev.spravedlivo.flibuster.viewmodel.BookSearchScreenViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val bookSearchScreenViewModel: BookSearchScreenViewModel by viewModels()
        super.onCreate(savedInstanceState)
        setContent {
            FlibusterApp(bookSearchScreenViewModel)
        }
    }
}

@Composable
fun FlibusterApp(
    bookSearchScreenViewModel: BookSearchScreenViewModel
) {

    val navHostController = rememberNavController()

    FlibusterTheme {
        Scaffold(bottomBar = { NavigationRow() }) {padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(navController = navHostController, BookSearch.route) {
                    composable(BookSearch.route) {
                        BookSearchScreen(bookSearchScreenViewModel) {
                            navHostController.navigateSingleTopTo(
                                Book.buildRoute(it.url),
                                stateSaving = false,
                                stateRestoring = false)
                        }
                    }
                    composable(Book.routeWithArgs, arguments = Book.arguments) { navBackStackEntry ->
                        val url =
                            navBackStackEntry.arguments?.getString(Book.bookUrl)
                        if (url == null) {
                            navHostController.navigateSingleTopTo(BookSearch.route)
                            return@composable
                        }
                        BookScreen(url = url)
                    }
                }

            }
        }

    }
}