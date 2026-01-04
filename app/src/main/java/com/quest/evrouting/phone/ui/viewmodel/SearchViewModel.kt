package com.quest.evrouting.phone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest.evrouting.phone.domain.model.Place
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái của màn hình tìm kiếm
data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<Place> = emptyList(),
    val recentHistory: List<Place> = emptyList(), // Sẽ dùng sau
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() { // TODO: Sẽ inject Repository vào đây

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Một Flow riêng để quản lý truy vấn tìm kiếm và áp dụng debounce
    private val _searchQuery = MutableStateFlow("")

    init {
        // Tải lịch sử tìm kiếm ban đầu
        loadRecentHistory()

        // Lắng nghe sự thay đổi của _searchQuery
        viewModelScope.launch {
            _searchQuery
                .debounce(2000L) // Chờ 2s sau khi người dùng ngừng gõ
                .collect { query ->
                    if (query.length > 2) {
                        searchPlaces(query)
                    } else {
                        // Nếu query quá ngắn, xóa kết quả tìm kiếm và hiển thị lại lịch sử
                        _uiState.update { it.copy(searchResults = emptyList()) }
                    }
                }
        }
    }

    // Hàm này sẽ được gọi từ UI
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    private fun searchPlaces(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // ---- GIẢ LẬP GỌI API ----
            kotlinx.coroutines.delay(500) // Giả lập độ trễ mạng
            val fakeResults = listOf(
                Place("res1", "Gợi ý cho '$query' 1", "Địa chỉ gợi ý 1"),
                Place("res2", "Gợi ý cho '$query' 2", "Địa chỉ gợi ý 2")
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    searchResults = fakeResults
                )
            }
            // ------------------------
            // TODO: Thay thế phần giả lập bằng lệnh gọi Repository thực sự
        }
    }

    private fun loadRecentHistory() {
        val fakeHistory = listOf(
            Place("1", "Sân Cầu Lông - Pickleball Kim Minh", "206/8A, Bình Quới..."),
            Place("2", "Nhà thờ Đức Bà Sài Gòn", "Công trường Công xã Paris..."),
            Place("3", "Quận 10", "Hồ Chí Minh City"),
            Place("4","Orte (near A1 Motorway), Viterbo, Lazio, Italy",null),
            Place("5","Rome City Center (Centro Storico), Lazio, Italy",null)
        )
        _uiState.update { it.copy(recentHistory = fakeHistory) }
    }
}
