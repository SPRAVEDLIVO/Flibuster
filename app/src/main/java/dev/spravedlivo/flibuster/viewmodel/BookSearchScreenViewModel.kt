package dev.spravedlivo.flibuster.viewmodel

import androidx.lifecycle.ViewModel
import dev.spravedlivo.flibuster.data.BookInfoSearch
import dev.spravedlivo.flibuster.data.BookSearchScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// MVI
class BookSearchScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(BookSearchScreenState())
    val state = _state.asStateFlow()
    fun updateUIBooks(list: List<BookInfoSearch>) {
        _state.update {currentState ->
            currentState.copy(
                list = list
            )
        }
    }
    fun errored(err: Boolean, text: String = "") {
        _state.update {currentState ->
            currentState.copy(
                displayError = err,
                errorText = text
            )
        }
    }
}