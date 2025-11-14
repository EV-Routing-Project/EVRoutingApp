package com.quest.evrounting.data.local.database

import com.quest.evrounting.data.model.dynamic.PortStatusLogs
import com.quest.evrounting.data.model.reference.*
import com.quest.evrounting.data.model.staticc.AddressInfos
import com.quest.evrounting.data.model.staticc.ChargePoints
import com.quest.evrounting.data.model.staticc.Connections
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Singleton object chịu trách nhiệm khởi tạo và quản lý kết nối cơ sở dữ liệu.
 */
object DatabaseFactory {

    fun createSchema() {
        // 1. Cấu hình thông tin kết nối đến MySQL
        val url = "jdbc:mysql://localhost:3306/ev_routing_db?useUnicode=true&serverTimezone=UTC"
        val driver = "com.mysql.cj.jdbc.Driver"
        val user = "root"
        val password = "1234"

        // 2. Thực hiện kết nối
        Database.connect(url, driver, user, password)

        // 3. Tự động tạo bảng nếu chúng chưa tồn tại
        // Lệnh này sẽ kiểm tra và chỉ tạo những bảng còn thiếu.
        transaction {
            SchemaUtils.create(
                // Bảng tham chiếu
                Countries,
                Operators,
                UsageTypes,
                ConnectionTypes,
                CurrentTypes,

                // Bảng dữ liệu tĩnh chính
                AddressInfos,
                ChargePoints,
                Connections,

                // Bảng dữ liệu động
                PortStatusLogs
            )
            println("✅ Schema ev_routing_db đã được tạo thành công.")
        }
    }

    // Lưu ý: đoạn code này chỉ chạy 1 lần duy nhất
    // Exposed sẽ không tạo lại bảng nếu chạy nhiều lần
    @JvmStatic
    fun main(args: Array<String>) {
        println("🚀 Bắt đầu quá trình tạo schema thủ công...")
        try {
            createSchema()
        } catch (e: Exception) {
            println("🚨 Đã xảy ra lỗi trong quá trình tạo schema:")
            e.printStackTrace()
        }
        println("🏁 Quá trình kết thúc.")
    }
}
