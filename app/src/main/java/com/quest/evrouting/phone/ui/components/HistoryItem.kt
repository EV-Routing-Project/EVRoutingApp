package com.quest.evrouting.phone.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.quest.evrouting.phone.domain.model.Place

//@Composable
//fun HistoryItem(
//    place: Place,
//    onItemClick: (Place) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable { onItemClick(place) } // Cho phép nhấn vào cả hàng
//            .padding(vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = Icons.Default.History,
//            contentDescription = "Recent search",
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = place.primaryText,
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            // Chỉ hiển thị dòng thứ 2 nếu nó không rỗng
//            if (place.secondaryText?.isNotEmpty() == true) {
//                Text(
//                    text = place.secondaryText,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//    }
//}

@Composable
fun HistoryItem(
    place: Place,
    onItemClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(place) }
            .padding(horizontal = 16.dp, vertical = 12.dp), // Thống nhất padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "Recent search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.primaryText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                // -> 2. XỬ LÝ TRÀN DÒNG
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            place.secondaryText?.takeIf { it.isNotEmpty() }?.let { secondary ->
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // -> 2. XỬ LÝ TRÀN DÒNG
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}