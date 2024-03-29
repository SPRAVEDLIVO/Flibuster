package dev.spravedlivo.flibuster.ui.book

import android.content.Context
import android.net.Uri
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spravedlivo.flibuster.R
import dev.spravedlivo.flibuster.Settings
import dev.spravedlivo.flibuster.network.FlibustaHelper
import dev.spravedlivo.flibuster.network.ResponseType
import dev.spravedlivo.flibuster.network.bookInfo
import dev.spravedlivo.flibuster.network.downloadBook
import dev.spravedlivo.flibuster.viewmodel.BookScreenViewModel
import kotlinx.coroutines.launch

// stateful :)
@Composable
fun BookScreen(context: Context, url: String) {
    val viewModel = viewModel<BookScreenViewModel>()
    val state by viewModel.state.collectAsState()

    val divisors = 2.dp
    
    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (state.showDetails == null) {
            Text(text = "Loading book info...")
            LaunchedEffect(Unit) {
                val loaded = bookInfo(context, url) { println(it) } ?: return@LaunchedEffect
                if (loaded.imageUrl != null) {
                    loaded.image = FlibustaHelper.imageFromUrl(context, loaded.imageUrl!!) { println(it) }
                }
                viewModel.updateShowDetails(loaded)
            }
        }
        else {
            Column(modifier = Modifier
                .fillMaxWidth()
                , verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
                if (state.showDetails!!.image == null) {
                        Text(stringResource(id = R.string.book_no_image))
                }
                else {
                    Image(bitmap = state.showDetails!!.image!!.asImageBitmap(), contentDescription = "Cover")
                }
            }

            Text(text = "Title", style=MaterialTheme.typography.titleLarge)
            Text(state.showDetails!!.title)
            Divider(thickness = divisors)

            Text(if (state.showDetails!!.authors.size > 1) "Authors" else "Author", style=MaterialTheme.typography.titleLarge)
            state.showDetails!!.authors.forEach {
                Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                    Text(text = it.name)
                }
            }
            Divider(thickness = divisors)
            if (state.showDetails!!.translators.isNotEmpty()) {
                Text(if (state.showDetails!!.translators.size > 1) "Translators" else "Translator", style=MaterialTheme.typography.titleLarge)
                state.showDetails!!.translators.forEach {
                    Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                        Text(text = it.name)
                    }
                }
                Divider(thickness = divisors)
            }
            if (state.showDetails!!.genres.isNotEmpty()) {
                Text(if (state.showDetails!!.genres.size > 1) "Genres" else "Genre", style=MaterialTheme.typography.titleLarge)
                state.showDetails!!.genres.forEach {
                    Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                        Text(text = it.name)
                    }
                }
                Divider(thickness = divisors)
            }

            if (state.showDetails!!.lemma.isNotBlank()) {
                Text("Lemma", style = MaterialTheme.typography.titleLarge)
                Text(state.showDetails!!.lemma)
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(onClick = { viewModel.updateShowPopup(true) }) {
                    Text("Download")
                }
            }
            val coroutineScope = rememberCoroutineScope()
            if (state.showPopup) {

                    Dialog(onDismissRequest = { viewModel.updateShowPopup(false)}) {
                        Column(verticalArrangement = Arrangement.Center, modifier = Modifier) {
                            state.showDetails!!.downloadFormats.forEach {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        downloadBook(
                                            context,
                                            "/b/${state.showDetails!!.url}/${it.second}",
                                            "${state.showDetails!!.url}.${it.first.substring(IntRange(1, it.first.length-2))}")
                                    }
                                    viewModel.updateShowPopup(false)
                                }) {
                                    Text(it.first)
                                }
                            }
                    }
                }

            }
        }
    }
}