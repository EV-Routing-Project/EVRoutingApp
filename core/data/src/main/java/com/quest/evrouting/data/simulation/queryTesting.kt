package com.quest.evrouting.data.simulation

import com.quest.evrouting.data.model.dynamic.PortStatusLog
import com.quest.evrouting.data.local.database.DatabaseFactory
import com.quest.evrouting.data.local.repository.POIRepository
import com.quest.evrouting.data.local.repository.PortStatusRepository
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

//fun main() = runBlocking {
//    val allConnectionID = POIRepository.getAllConnetionIDs()
//    val timestamp = TimeUnit.HOURS.toMillis(12)
//    Clock.start()
//    val statuses = PortStatusRepository.getStatusesAtTimestampForConnections(allConnectionID, timestamp)
//    println("Thực hiện tác vụ đã cho trong: ${Clock.getCurrentTimestamp()} ")
//    if (statuses.size == allConnectionID.size) {
//        println("Đã lấy đúng thông tin của ${statuses.size} connections")
//    }
//}

fun main() = runBlocking {
    DatabaseFactory.connect(
        url = DatabaseFactory.URL,
        driver = DatabaseFactory.DRIVER,
        user = DatabaseFactory.USER,
        password = DatabaseFactory.PASSWORD
    )

    val totalConnections = POIRepository.getAllConnetionIDs().size
    println("✅ Hệ thống có tổng cộng $totalConnections connections.")

//    val testTimestamp = TimeUnit.HOURS.toMillis(3)
    val testTimestamp = TimeUnit.MINUTES.toMillis(12)

    println(
        "   -> Kiểm tra trạng thái tại mốc thời gian: $testTimestamp ms (${TimeUnit.MILLISECONDS.toHours(testTimestamp)} giờ)"
    )

    println("\n🚀 Bắt đầu thực thi và đo lường hàm getLatestStatusForAllConnections...")
    var duration: Long
    val statuses: List<PortStatusLog> = try {
        lateinit var queryResult: List<PortStatusLog>

        duration = measureTimeMillis {
            // Gọi hàm mới cần đo lường
            queryResult =
                PortStatusRepository.getLatestStatusForAllConnections(testTimestamp)
        }
        queryResult
    } catch (e: Exception) {
        println("🚨 Đã xảy ra lỗi trong quá trình truy vấn: ${e.message}")
        e.printStackTrace()
        return@runBlocking
    }

    println("\n📊 ----- KẾT QUẢ KIỂM TRA -----")
    println("   - Thời gian thực thi: $duration ms")
    println("   - Số lượng bản ghi trạng thái trả về: ${statuses.size}")

    if (statuses.isNotEmpty() && statuses.size == totalConnections) {
        println("✅ Thành công: Số lượng bản ghi trả về đã khớp với tổng số connection (${statuses.size} bản ghi).")
    } else if (statuses.isEmpty()) {
        println("⚠️ Cảnh báo: Không tìm thấy bản ghi nào tại mốc thời gian này. Có thể không có sự kiện nào xảy ra trong khoảng 5 phút đó.")
    } else {
        println("❌ Thất bại: Số lượng bản ghi trả về (${statuses.size}) không khớp với tổng số connection (${totalConnections})!")
    }
    println("---------------------------------")
}