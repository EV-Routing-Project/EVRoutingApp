// Trong file HomeScreen.kt

package com.example.frontend.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontend.ui.theme.EVRountingAppTheme

// Dữ liệu giả để hiển thị lên giao diện
// Sau này, chúng ta sẽ thay thế bằng dữ liệu thật từ ViewModel
data class ChargePoint(val id: Int, val address: String, val availablePorts: Int, val totalPorts: Int)

val fakeChargePoints = listOf(
    ChargePoint(1, "123 Đường Lê Lợi, Quận 1, TP.HCM", 3, 4),
    ChargePoint(2, "456 Đường Nguyễn Huệ, Quận 1, TP.HCM", 1, 2),
    ChargePoint(3, "789 Đường Pasteur, Quận 3, TP.HCM", 8, 8),
    ChargePoint(4, "Khu Công nghệ cao, Quận 9, TP.HCM", 0, 4),
    ChargePoint(5, "Tòa nhà Bitexco, Quận 1, TP.HCM", 5, 5),

    ChargePoint(1, "123 Đường Lê Lợi, Quận 1, TP.HCM", 3, 4),
    ChargePoint(2, "456 Đường Nguyễn Huệ, Quận 1, TP.HCM", 1, 2),
    ChargePoint(3, "789 Đường Pasteur, Quận 3, TP.HCM", 8, 8),
    ChargePoint(4, "Khu Công nghệ cao, Quận 9, TP.HCM", 0, 4),
    ChargePoint(5, "Tòa nhà Bitexco, Quận 1, TP.HCM", 5, 5),
    ChargePoint(1, "123 Đường Lê Lợi, Quận 1, TP.HCM", 3, 4),
    ChargePoint(2, "456 Đường Nguyễn Huệ, Quận 1, TP.HCM", 1, 2),
    ChargePoint(3, "789 Đường Pasteur, Quận 3, TP.HCM", 8, 8),
    ChargePoint(4, "Khu Công nghệ cao, Quận 9, TP.HCM", 0, 4),
    ChargePoint(5, "Tòa nhà Bitexco, Quận 1, TP.HCM", 5, 5),
)

/**
 * Composable cho toàn bộ màn hình chính
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // Scaffold là một layout cơ bản, cung cấp sẵn các vị trí cho
    // TopAppBar (thanh trên), BottomBar (thanh dưới), FloatingActionButton...
    Scaffold(
        topBar = {
            // Đây là thanh tiêu đề của ứng dụng
            TopAppBar(
                title = { Text("Trạm Sạc Quanh Đây") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Màu nền
                    titleContentColor = Color.White // Màu chữ
                )
            )
        }
    ) { innerPadding ->
        // innerPadding chứa thông tin về kích thước của TopAppBar,
        // giúp nội dung không bị thanh tiêu đề che khuất.
        Column(modifier = Modifier.padding(innerPadding)) {
            // Hiển thị danh sách các trạm sạc
            ChargePointList(chargePoints = fakeChargePoints)
        }
    }
}

/**
 * Composable để hiển thị danh sách các trạm sạc
 */
@Composable
fun ChargePointList(chargePoints: List<ChargePoint>, modifier: Modifier = Modifier) {
    // LazyColumn rất hiệu quả, chỉ render những item hiện trên màn hình
    // Tương đương với RecyclerView trong XML
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        // Lặp qua danh sách và tạo một ChargePointItem cho mỗi trạm sạc
        items(chargePoints) { point ->
            ChargePointItem(chargePoint = point)
            Spacer(modifier = Modifier.height(12.dp)) // Thêm khoảng cách giữa các item
        }
    }
}

/**
 * Composable cho MỘT item trong danh sách
 */
@Composable
fun ChargePointItem(chargePoint: ChargePoint, modifier: Modifier = Modifier) {
    // Card tạo một khung viền bo góc và có đổ bóng rất đẹp
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = chargePoint.address,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Trạng thái: ${chargePoint.availablePorts} / ${chargePoint.totalPorts} cổng trống",
                style = MaterialTheme.typography.bodyMedium,
                // Thay đổi màu chữ tùy theo trạng thái
                color = if (chargePoint.availablePorts > 0) Color(0xFF008800) else Color.Red
            )
        }
    }
}


// --- Các hàm xem trước (Preview) ---

@Preview(showBackground = true)
@Composable
fun ChargePointItemPreview() {
    EVRountingAppTheme {
        ChargePointItem(chargePoint = fakeChargePoints[0])
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreview() {
    EVRountingAppTheme {
        HomeScreen()
    }
}
