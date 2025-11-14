package com.quest.evrounting.data.mapper


// Import các lớp DTO từ module apiservice
import com.quest.evrounting.apiservice.ocm.model.AddressInfo as AddressInfoApi
import com.quest.evrounting.apiservice.ocm.model.Connections as ConnectionApi
import com.quest.evrounting.apiservice.ocm.pois.PoisResponse as ChargePointApi

// Import các lớp Entity từ module database
import com.quest.evrounting.data.model.staticc.AddressInfo
import com.quest.evrounting.data.model.staticc.Connection
import com.quest.evrounting.data.model.staticc.ChargePoint

/**
 * File này chứa các hàm mở rộng (extension functions) để chuyển đổi (map)
 * các đối tượng DTO (Data Transfer Object) từ API thành các đối tượng Entity
 * để lưu trữ trong cơ sở dữ liệu địa phương (Room).
 */

object ToEntity {

// 1. Mapper cho đối tượng trạm sạc chính (ChargePoint)
// =================================================================

    /**
     * Chuyển đổi một đối tượng [ChargePointApi] (PoisResponse từ API)
     * thành một đối tượng [ChargePoint] để lưu vào database.
     */
    fun ChargePointApi.toEntity(): ChargePoint {
        return ChargePoint(
            id = this.id, // Ánh xạ trực tiếp ID
            uuid = this.uuid,
            addressInfoId = this.addressInfo.id,
            operatorId = this.operatorInfo.id,
            usageTypeId = this.usageType.id,
            // Lưu ý: Các trường không có trong API nhưng có trong Entity
            // sẽ cần được gán giá trị mặc định hoặc null tại đây.
        )
    }

// 2. Mapper cho đối tượng thông tin địa chỉ (AddressInfo)
// =================================================================

    /**
     * Chuyển đổi một đối tượng [AddressInfoApi] từ API
     * thành một đối tượng [AddressInfo] để lưu vào database.
     */
    fun AddressInfoApi.toEntity(): AddressInfo {
        return AddressInfo(
            id = this.id,
            title = this.title ?: "N/A",
            addressLine1 = this.addressLine1 ?: "N/A",
            town = this.town ?: "N/A",
            postcode = this.postcode ?: "N/A",
            countryId = this.country.id,
            latitude = this.latitude,
            longitude = this.longitude,
            accessComments = this.accessComments ?: "N/A",
            geohash12 = this.geohash12,
        )
    }

// 3. Mapper cho đối tượng cổng kết nối (Connection)
// =================================================================

    /**
     * Chuyển đổi một đối tượng [ConnectionApi] từ API
     * thành một đối tượng [Connection] để lưu vào database.
     *
     * @param chargePointId Khóa ngoại để liên kết cổng sạc này với trạm sạc.
     */
    fun ConnectionApi.toEntity(chargePointId: Int): Connection {
        return Connection(
            id = this.id,
            chargePointId = chargePointId, // Gán khóa ngoại
            connectionTypeId = this.connectionType.id,
            powerKw = this.powerKW,
            currentTypeId = this.currentType?.id,
            quantity = this.quantity
        )
    }
}