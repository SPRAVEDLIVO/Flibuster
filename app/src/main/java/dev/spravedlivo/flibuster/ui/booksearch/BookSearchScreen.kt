package dev.spravedlivo.flibuster.ui.booksearch

import android.content.Context
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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.spravedlivo.flibuster.data.BookInfoSearch
import dev.spravedlivo.flibuster.network.searchBooks
import dev.spravedlivo.flibuster.ui.book.BookRow
import dev.spravedlivo.flibuster.viewmodel.BookSearchScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun BookSearchScreen(context: Context, onClickBook: (BookInfoSearch) -> Unit) {
    val viewModel = viewModel<BookSearchScreenViewModel>()
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        BookSearch(onSearch = {
            coroutineScope.launch {
                searchBooks(context, it, { books ->
                    viewModel.updateUIBooks(books)
                }, { message ->
                    viewModel.errored(true, message)
                })
            }
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