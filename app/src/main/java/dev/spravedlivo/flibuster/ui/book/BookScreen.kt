package dev.spravedlivo.flibuster.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.spravedlivo.flibuster.R
import dev.spravedlivo.flibuster.data.BookInfo
import dev.spravedlivo.flibuster.network.FlibustaHelper
import dev.spravedlivo.flibuster.network.bookInfo


@Composable
fun BookScreen(url: String) {
    var showDetails: BookInfo? by remember { mutableStateOf(null) }
    val divisors = 2.dp
    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (showDetails == null) {
            Text(text = "Loading book info...")
            LaunchedEffect(Unit) {
                val loaded = bookInfo(url) { println(it) }
                if (loaded.imageUrl != null) {
                    println(loaded.imageUrl)
                    loaded.image = FlibustaHelper.imageFromUrl(loaded.imageUrl!!) { println(it) }
                }
                showDetails = loaded
            }
        }
        else {
            Column(modifier = Modifier
                .fillMaxWidth()
                , verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
                if (showDetails!!.image == null) {
                        Text(stringResource(id = R.string.book_no_image))
                }
                else {
                    Image(bitmap = showDetails!!.image!!.asImageBitmap(), contentDescription = "Cover")
                }
            }

            Text(text = "Title", style=MaterialTheme.typography.titleLarge)
            Text(showDetails!!.title)
            Divider(thickness = divisors)

            Text(if (showDetails!!.authors.size > 1) "Authors" else "Author", style=MaterialTheme.typography.titleLarge)
            showDetails!!.authors.forEach {
                Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                    Text(text = it.name)
                }
            }
            Divider(thickness = divisors)
            if (showDetails!!.translators.isNotEmpty()) {
                Text(if (showDetails!!.translators.size > 1) "Translators" else "Translator", style=MaterialTheme.typography.titleLarge)
                showDetails!!.translators.forEach {
                    Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                        Text(text = it.name)
                    }
                }
                Divider(thickness = divisors)
            }
            if (showDetails!!.genres.isNotEmpty()) {
                Text(if (showDetails!!.genres.size > 1) "Genres" else "Genre", style=MaterialTheme.typography.titleLarge)
                showDetails!!.genres.forEach {
                    Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(3.dp)) {
                        Text(text = it.name)
                    }
                }
                Divider(thickness = divisors)
            }

            if (showDetails!!.lemma.isNotBlank()) {
                Text("Lemma", style = MaterialTheme.typography.titleLarge)
                Text(showDetails!!.lemma)
            }
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(onClick = { /*TODO*/ }) {
                    Text("Download")
                }
            }

        }
    }
}