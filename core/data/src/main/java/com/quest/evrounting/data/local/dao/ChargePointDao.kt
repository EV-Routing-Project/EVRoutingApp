package com.quest.evrounting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quest.evrounting.data.model.staticc.ChargePointEntity
import com.quest.evrounting.data.model.staticc.ConnectionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object cho các bảng dữ liệu chính: ChargePoint và Connection.
 * Vì Connections (các loại cổng sạc) luôn gắn liền với một ChargePoint (trạm sạc),
 * việc đặt các hàm truy vấn của nó ngay trong ChargePointDao là một lựa chọn rất hợp lý.
 */
@Dao
interface ChargePointDao {

    // --- ChargePoint Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChargePoints(chargePoints: List<ChargePointEntity>)

    /**
     * @return Một Flow chứa trạm sạc hoặc null nếu không tìm thấy.
     */
    @Query("SELECT * FROM ChargePoint WHERE ID = :id")
    fun getChargePointById(id: Int): Flow<ChargePointEntity?>


    @Query("SELECT * FROM ChargePoint")
    fun getAllChargePoints(): Flow<List<ChargePointEntity>>


    // --- Connection Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnections(connections: List<ConnectionEntity>)

    /**
     * @return Một Flow chứa danh sách các Connection của trạm sạc đó.
     */
    @Query("SELECT * FROM Connections WHERE ChargePointID = :chargePointId")
    fun getConnectionsForChargePoint(chargePointId: Int): Flow<List<ConnectionEntity>>
}
