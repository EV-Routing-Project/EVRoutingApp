package com.quest.evrouting.data.local.repository

import com.quest.evrouting.data.model.reference.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Repository quản lý việc truy cập vào các bảng dữ liệu tham chiếu
 */
object ReferenceRepository {

    //region --- HÀM CHUYỂN ĐỔI (CONVERTERS) ---
    // Chuyển đổi dữ liệu từ database sang đối tượng kotlin
    private fun toCountry(row: ResultRow): Country = Country(
        id = row[Countries.id],
        title = row[Countries.title],
        isoCode = row[Countries.isoCode],
    )

    private fun toOperator(row: ResultRow): Operator = Operator(
        id = row[Operators.id],
        title = row[Operators.title],
        websiteUrl = row[Operators.websiteUrl],
    )

    private fun toConnectionType(row: ResultRow): ConnectionType = ConnectionType(
        id = row[ConnectionTypes.id],
        title = row[ConnectionTypes.title],
        formalName = row[ConnectionTypes.formalName]
    )

    private fun toCurrentType(row: ResultRow): CurrentType = CurrentType(
        id = row[CurrentTypes.id],
        title = row[CurrentTypes.title]
    )

    private fun toUsageType(row: ResultRow): UsageType = UsageType(
        id = row[UsageTypes.id],
        title = row[UsageTypes.title],
    )
    //endregion


//region --- HÀM CHÈN/CẬP NHẬT DỮ LIỆU (UPSERT) ---

    suspend fun upsertCountries(countries: List<Country>) {
        newSuspendedTransaction<Unit> {
            Countries.batchUpsert(countries) { country ->
                this[Countries.id] = country.id
                this[Countries.title] = country.title
                this[Countries.isoCode] = country.isoCode
            }
        }
    }

    suspend fun upsertConnectionTypes(connectionTypes: List<ConnectionType>) {
        newSuspendedTransaction<Unit> {
            ConnectionTypes.batchUpsert(connectionTypes) { type ->
                this[ConnectionTypes.id] = type.id
                this[ConnectionTypes.title] = type.title
                this[ConnectionTypes.formalName] = type.formalName
            }
        }
    }

    suspend fun upsertOperators(operators: List<Operator>) {
        newSuspendedTransaction<Unit> {
            Operators.batchUpsert(operators) { operator ->
                this[Operators.id] = operator.id
                this[Operators.title] = operator.title
                this[Operators.websiteUrl] = operator.websiteUrl
            }
        }
    }

    suspend fun upsertUsageTypes(usageTypes: List<UsageType>) {
        newSuspendedTransaction<Unit> {
            UsageTypes.batchUpsert(usageTypes) { type ->
                this[UsageTypes.id] = type.id
                this[UsageTypes.title] = type.title
            }
        }
    }

    suspend fun upsertCurrentTypes(currentTypes: List<CurrentType>) {
        newSuspendedTransaction<Unit> {
            CurrentTypes.batchUpsert(currentTypes) { type ->
                this[CurrentTypes.id] = type.id
                this[CurrentTypes.title] = type.title
            }
        }
    }

//endregion


    //region --- HÀM TRUY VẤN (GET) ---
    // dùng Flow để xử lý bất đồng bộ, tránh làm "đóng băng" giao diện người dùng

    fun getAllCountries(): Flow<List<Country>> = flow {
        val items = newSuspendedTransaction {
            Countries.selectAll()
                .orderBy(Countries.title to SortOrder.ASC)
                .map(::toCountry)
        }
        emit(items)
    }

    fun getAllConnectionTypes(): Flow<List<ConnectionType>> = flow {
        val items = newSuspendedTransaction {
            ConnectionTypes.selectAll()
                .orderBy(ConnectionTypes.title to SortOrder.ASC)
                .map(::toConnectionType)
        }
        emit(items)
    }

    fun getAllOperators(): Flow<List<Operator>> = flow {
        val items = newSuspendedTransaction {
            Operators.selectAll()
                .orderBy(Operators.title to SortOrder.ASC)
                .map(::toOperator)
        }
        emit(items)
    }

    fun getAllUsageTypes(): Flow<List<UsageType>> = flow {
        val items = newSuspendedTransaction {
            UsageTypes.selectAll()
                .orderBy(UsageTypes.title to SortOrder.ASC)
                .map(::toUsageType)
        }
        emit(items)
    }

    fun getAllCurrentTypes(): Flow<List<CurrentType>> = flow {
        val items = newSuspendedTransaction {
            CurrentTypes.selectAll()
                .orderBy(CurrentTypes.title to SortOrder.ASC)
                .map(::toCurrentType)
        }
        emit(items)
    }

    //endregion
}

