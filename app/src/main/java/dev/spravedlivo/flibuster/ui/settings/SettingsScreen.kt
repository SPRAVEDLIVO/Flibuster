package dev.spravedlivo.flibuster.ui.settings

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spravedlivo.flibuster.MainActivity
import dev.spravedlivo.flibuster.Settings
import dev.spravedlivo.flibuster.viewmodel.SettingsScreenViewModel


fun launchLauncher(context: Context, initial: Uri) {
    (context as MainActivity).openDocumentTreeLauncher.launch(initial)
}

@Composable
fun SettingsScreen(context: Context) {
    val viewModel = viewModel<SettingsScreenViewModel>()
    viewModel.readSettings(context)
    val state by viewModel.state.collectAsState()

    if (!state.loaded) {
        Text("Reading settings...")
        return
    }


    var value by remember {
        mutableStateOf(
            Settings.readFromPreferencesOrDefault(
                state.preferences!!,
                "flibusta_url"
            )
        )
    }
    var read by remember { mutableStateOf(Settings.readFromPreferencesOrDefault(state.preferences!!, "download_folder", "")!!) }

    viewModel.register { a, b ->
        read = b
    }
    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Website URL:")
            TextField(
                value = value!!,
                singleLine = true,
                onValueChange = { value = it },
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.updateKey(context, "flibusta_url", value!!)
                })
            )
        }

        Row {
            Text(text = "Download folder: ")

            if (read.isEmpty()) Text(
                "Choose folder",
                color = MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.clickable {
                    launchLauncher(context, Uri.EMPTY)
                }) else Text(
                read,
                modifier = Modifier.clickable { launchLauncher(context, Uri.parse(read)) },
                color = MaterialTheme.colorScheme.inversePrimary)
        }
    }
}