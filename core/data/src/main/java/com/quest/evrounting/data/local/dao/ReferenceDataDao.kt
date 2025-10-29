package com.quest.evrounting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quest.evrounting.data.model.reference.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object cho tất cả các bảng dữ liệu tham chiếu (Reference Tables).
 * Gộp chung để dễ quản lý.
 */
@Dao
interface ReferenceDataDao {

    // --- RefCountry ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<RefCountryEntity>)
    @Query("SELECT * FROM RefCountry ORDER BY Title ASC")
    fun getAllCountries(): Flow<List<RefCountryEntity>>

    // --- RefConnectionType ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectionTypes(connectionTypes: List<RefConnectionTypeEntity>)
    @Query("SELECT * FROM RefConnectionType ORDER BY Title ASC")
    fun getAllConnectionTypes(): Flow<List<RefConnectionTypeEntity>>

    // --- RefOperator ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperators(operators: List<RefOperatorEntity>)
    @Query("SELECT * FROM RefOperator ORDER BY Title ASC")
    fun getAllOperators(): Flow<List<RefOperatorEntity>>

    // --- RefUsageType ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageTypes(usageTypes: List<RefUsageTypeEntity>)
    @Query("SELECT * FROM RefUsageType ORDER BY Title ASC")
    fun getAllUsageTypes(): Flow<List<RefUsageTypeEntity>>

    // --- RefCurrentType ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentTypes(currentTypes: List<RefCurrentTypeEntity>)
    @Query("SELECT * FROM RefCurrentType")
    fun getAllCurrentTypes(): Flow<List<RefCurrentTypeEntity>>

    // --- RefStatusType ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusTypes(statusTypes: List<RefStatusTypeEntity>)
    @Query("SELECT * FROM RefStatusType")
    fun getAllStatusTypes(): Flow<List<RefStatusTypeEntity>>
}
