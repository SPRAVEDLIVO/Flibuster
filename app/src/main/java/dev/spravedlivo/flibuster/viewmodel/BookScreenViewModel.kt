package dev.spravedlivo.flibuster.viewmodel

import androidx.lifecycle.ViewModel
import dev.spravedlivo.flibuster.data.BookInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookScreenState(val showDetails: BookInfo?, val showPopup: Boolean)

// MVI
class BookScreenViewModel: ViewModel() {
    private val _state = MutableStateFlow(BookScreenState(null, false))
    val state = _state.asStateFlow()
    fun updateShowDetails(showDetails: BookInfo?) {
        _state.update { currentState ->
            currentState.copy(showDetails = showDetails)
        }
    }
    fun updateShowPopup(showPopup: Boolean) {
        _state.update { currentState ->
            currentState.copy(showPopup = showPopup)
        }
    }
}