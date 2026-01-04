package com.quest.evrouting.data.local.database

import com.quest.evrouting.data.model.dynamic.PortStatusLogs
import com.quest.evrouting.data.model.reference.*
import com.quest.evrouting.data.model.staticc.AddressInfos
import com.quest.evrouting.data.model.staticc.ChargePoints
import com.quest.evrouting.data.model.staticc.Connections
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Singleton object chịu trách nhiệm khởi tạo và quản lý kết nối cơ sở dữ liệu.
 */
object DatabaseFactory {
    const val URL = "jdbc:mysql://localhost:3306/ev_routing_db?useUnicode=true&serverTimezone=UTC"
    const val DRIVER = "com.mysql.cj.jdbc.Driver"
    const val USER = "root"
    const val PASSWORD = "1234"

    fun connect(url: String, driver: String, user: String, password: String){
        Database.connect(url, driver, user, password)
        println("✅ Kết nối cơ sở dữ liệu thành công.")
    }

    // Trước khi chạy createSchema phải tạo sẵn Schema ev_routing_db trong CSDL
    fun createSchema(url: String, driver: String, user: String, password: String) {
        connect(url, driver, user, password)

        transaction {
            // SchemaUtils.create(...) chỉ tạo bảng nếu chưa tồn tại
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
}
