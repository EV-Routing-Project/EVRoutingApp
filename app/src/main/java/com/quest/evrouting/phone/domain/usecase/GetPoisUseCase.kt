package com.quest.evrouting.phone.domain.usecase

import com.quest.evrouting.phone.domain.model.POI
import com.quest.evrouting.phone.domain.repository.PoiRepository

class GetPoisUseCase(
    private val repository: PoiRepository
) {
    /**
     * Ghi đè toán tử invoke để lớp này có thể được gọi như một hàm.
     * Ví dụ: getPoisUseCase() thay vì getPoisUseCase.execute()
     */
    suspend operator fun invoke(): List<POI> {
        // Logic nghiệp vụ có thể được thêm vào đây.
        // Ví dụ: lọc, sắp xếp, hoặc kết hợp dữ liệu từ nhiều nguồn.
        // Hiện tại, chỉ cần gọi thẳng đến repository.
        return repository.getAllPois()
    }
}
