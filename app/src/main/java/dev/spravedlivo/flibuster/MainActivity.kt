package dev.spravedlivo.flibuster

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dev.spravedlivo.flibuster.extensions.navigateSingleTopTo
import dev.spravedlivo.flibuster.ui.components.NavigationRow
import dev.spravedlivo.flibuster.ui.theme.FlibusterTheme
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : ComponentActivity() {
    lateinit var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openDocumentTreeLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            if (it == null) return@registerForActivityResult
            val ctx = this as Context
            lifecycleScope.launch {
                Settings.save(ctx, "download_folder", it.toString())
            }
        }

        setContent {
            FlibusterApp(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::openDocumentTreeLauncher.isInitialized) {
            openDocumentTreeLauncher.unregister()
        }
    }
}

@Composable
fun FlibusterApp(
    context: Context,
) {
    val navHostController = rememberNavController()
    FlibusterTheme {
        Scaffold(bottomBar = { NavigationRow { navHostController.navigateSingleTopTo(SettingsRoute.route) } }) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(context, navHostController = navHostController)
            }
        }
    }
}