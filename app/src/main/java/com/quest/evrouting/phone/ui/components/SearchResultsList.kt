package com.quest.evrouting.phone.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quest.evrouting.phone.domain.model.Place

@Composable
fun SearchResultsList(
    places: List<Place>,
    isLoading: Boolean,
    onItemClick: (Place) -> Unit
) {
    if (isLoading) {
        // Hiển thị vòng xoay loading ở giữa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(places) { place ->
                // Tạm thời dùng lại HistoryItem, bạn có thể tạo Composable riêng nếu muốn
                HistoryItem(place = place, onItemClick = onItemClick)
                Divider(modifier = Modifier.padding(start = 56.dp))
            }
        }
    }
}