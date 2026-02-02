package com.quest.evrouting.phone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.ui.components.HistoryItem
import com.quest.evrouting.phone.ui.components.RecentHistoryList
import com.quest.evrouting.phone.ui.components.SearchResultsList
import com.quest.evrouting.phone.ui.components.SearchTextField
import com.quest.evrouting.phone.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onNavigateBackWithResult: (origin: Place, destination: Place) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // State lưu trữ lựa chọn người dùng
    var selectedOrigin by remember { mutableStateOf<Place?>(null) }
    var selectedDestination by remember { mutableStateOf<Place?>(null) }

    // State lưu trữ văn bản nhập vào
    var originInputText by remember { mutableStateOf("") }
    var destinationInputText by remember { mutableStateOf("") }

    // State xác định ô nào đang được focus
    var isOriginFocused by remember { mutableStateOf(false) }
    var isDestinationFocused by remember { mutableStateOf(false) }

    val onPlaceSelected = { place: Place ->
        if (isOriginFocused) {
            selectedOrigin = place
            originInputText = place.primaryText
        } else if (isDestinationFocused) {
            selectedDestination = place
            destinationInputText = place.primaryText
        }
        viewModel.onSearchQueryChanged("")
    }

    val showSearchResults = uiState.searchQuery.isNotEmpty()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val originToSend = selectedOrigin ?: Place(
                            id = "manual_origin",
                            primaryText = originInputText.trim(),
                            secondaryText = "Manually entered"
                        )
                        val destinationToSend = selectedDestination ?: Place(
                            id = "manual_destination",
                            primaryText = destinationInputText.trim(),
                            secondaryText = "Manually entered"
                        )
                        onNavigateBackWithResult(originToSend, destinationToSend)
                    },
                    enabled = (selectedOrigin != null || originInputText.isNotBlank()) &&
                            (selectedDestination != null || destinationInputText.isNotBlank())
                ) {
                    Text("Done")
                }
            }

            // --- KHỐI NHẬP LIỆU ---
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                // Ô điểm đi
                SearchTextField(
                    modifier = Modifier.onFocusChanged { focusState ->
                        isOriginFocused = focusState.isFocused
                        if (!focusState.isFocused && selectedOrigin == null) {
                            viewModel.onSearchQueryChanged("")
                        }
                    },
                    value = originInputText,
                    onValueChange = { newText ->
                        if (originInputText != newText) {
                            selectedOrigin = null
                        }
                        originInputText = newText
                        viewModel.onSearchQueryChanged(newText)
                    },
                    placeholderText = "Your location",
                    icon = Icons.Default.Circle,
                    iconTint = Color.Blue
                )

                Spacer(Modifier.height(16.dp))

                // Ô điểm đến
                SearchTextField(
                    modifier = Modifier.onFocusChanged { focusState ->
                        isDestinationFocused = focusState.isFocused
                        if (!focusState.isFocused && selectedDestination == null) {
                            viewModel.onSearchQueryChanged("")
                        }
                    },
                    value = destinationInputText,
                    onValueChange = { newText ->
                        if (destinationInputText != newText) {
                            selectedDestination = null
                        }
                        destinationInputText = newText
                        viewModel.onSearchQueryChanged(newText)
                    },
                    placeholderText = "Choose destination",
                    icon = Icons.Default.LocationOn,
                    iconTint = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- DANH SÁCH HIỂN THỊ ĐỘNG ---
            if (showSearchResults) {
                // Nếu có query, hiển thị KẾT QUẢ TÌM KIẾM
                SearchResultsList(
                    places = uiState.searchResults,
                    isLoading = uiState.isLoading,
                    onItemClick = onPlaceSelected
                )
            } else {
                // Nếu không có query, hiển thị LỊCH SỬ
                RecentHistoryList(
                    places = uiState.recentHistory,
                    onItemClick = onPlaceSelected
                )
            }
        }
    }
}
