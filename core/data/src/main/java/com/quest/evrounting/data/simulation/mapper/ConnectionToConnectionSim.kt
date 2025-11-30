package com.quest.evrounting.data.simulation.mapper

import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.model.staticc.Connection
import com.quest.evrounting.data.simulation.ConnectionSim

// Khai báo nullable ConnectionSim? để lỡ chargePointId ánh xạ sai
suspend fun Connection.toConnectionSim(): ConnectionSim? {
    val addressInfo = POIRepository.getAddressInfoForChargePoint(this.chargePointId)
    if (addressInfo == null) {
        println("⚠️ Cảnh báo: Không tìm thấy AddressInfo cho ChargePoint ID #${this.chargePointId}. Connection #${this.id} sẽ bị bỏ qua.")
        return null
    }
    return ConnectionSim(
        id = this.id,
        powerKw = this.powerKw ?: 22.0,
        addressInfo = addressInfo,
    )
}


