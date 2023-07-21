package dev.spravedlivo.flibuster.ui.booksearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.spravedlivo.flibuster.R

@Composable
fun BookSearch(onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TextField(
            value = text,
            singleLine = true,
            onValueChange = { text = it },
            keyboardActions = KeyboardActions(onDone = { onSearch(text) }),
            label = { Text(stringResource(id = R.string.book_searchbar_hint)) },
        )
        Button(onClick = {
            onSearch(text)
        }, modifier = Modifier.padding(end=10.dp)) {
            Text(text = stringResource(id = R.string.book_searchbar_text))
        }
    }
}