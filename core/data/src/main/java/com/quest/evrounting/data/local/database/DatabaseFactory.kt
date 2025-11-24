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

    private const val URL = "jdbc:mysql://localhost:3306/ev_routing_db?useUnicode=true&serverTimezone=UTC"
    private const val DRIVER = "com.mysql.cj.jdbc.Driver"
    private const val USER = "root"
    private const val PASSWORD = "1234"

    fun connect(){
        Database.connect(URL, DRIVER, USER, PASSWORD)
        println("✅ Kết nối cơ sở dữ liệu thành công.")
    }
    fun createSchema() {
        connect()

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
}
