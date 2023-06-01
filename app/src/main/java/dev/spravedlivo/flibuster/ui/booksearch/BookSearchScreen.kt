package dev.spravedlivo.flibuster.ui.booksearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.spravedlivo.flibuster.data.BookInfoSearch
import dev.spravedlivo.flibuster.network.searchBooks
import dev.spravedlivo.flibuster.ui.components.BookRow
import dev.spravedlivo.flibuster.ui.components.BookSearch
import dev.spravedlivo.flibuster.viewmodel.BookSearchScreenViewModel

@Composable
fun BookSearchScreen(viewModel: BookSearchScreenViewModel, onClickBook: (BookInfoSearch) -> Unit) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        BookSearch(onSearch = {
            searchBooks(coroutineScope, it, { books ->
                viewModel.updateUIBooks(books)
            }, { message ->
                viewModel.errored(true, message)
            })
        }, modifier = Modifier.fillMaxWidth())
        if (!state.displayError) {
            LazyColumn {
                items(state.list) { bookUI ->
                    BookRow(row = bookUI, onClickBook = { onClickBook(it) })
                }
            }
        } else {
            Column {
                Text(text = state.errorText)
            }
        }
    }
}