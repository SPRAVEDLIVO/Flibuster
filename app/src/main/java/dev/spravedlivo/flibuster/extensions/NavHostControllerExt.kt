package dev.spravedlivo.flibuster.extensions

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.navigateSingleTopTo(route: String, stateSaving: Boolean = true, stateRestoring: Boolean = true) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = stateSaving
        }
        launchSingleTop = true
        restoreState = stateRestoring
    }
