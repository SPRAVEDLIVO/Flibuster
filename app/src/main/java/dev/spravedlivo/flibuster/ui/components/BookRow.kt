package dev.spravedlivo.flibuster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.spravedlivo.flibuster.data.BookInfoSearch
import dev.spravedlivo.flibuster.extensions.fadingEdge


@Composable
fun BookRow(row: BookInfoSearch, onClickBook: (BookInfoSearch) -> Unit) {
    val fade = Brush.horizontalGradient(0.9f to Color.Red, 1f to Color.Transparent)
    Column(
        Modifier
            .fadingEdge(fade)
            .padding(PaddingValues(top = 2.dp, bottom = 5.dp))
            .background(
                color = MaterialTheme.colorScheme.inversePrimary,
                shape = RoundedCornerShape(3.dp)
            )
            .clickable { onClickBook(row) }
            .fillMaxWidth()) {
        Text(row.title, maxLines = 1, overflow = TextOverflow.Clip, minLines = 1)
        row.authors.forEach {
            Text(it.name, modifier = Modifier.padding(PaddingValues(start = 5.dp, bottom = 2.dp))
                    .background(MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(3.dp)),
                    maxLines = 1, minLines = 1)
        }
    }
}


