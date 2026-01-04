package com.quest.evrouting.phone.domain.usecase

import com.quest.evrouting.phone.domain.model.ChargePoint
import com.quest.evrouting.phone.domain.repository.ChargePointRepository

/**
 * Một UseCase cụ thể chỉ chịu trách nhiệm cho một nghiệp vụ duy nhất: lấy danh sách các điểm sạc.
 * Nó không biết gì về ViewModel hay UI.
 */

// Là bộ lọc khi lấy dữ liệu
class GetChargePointsUseCase(
    private val repository: ChargePointRepository // Phụ thuộc vào "hợp đồng", không phụ thuộc vào "chi tiết"
) {

    /**
     * Ghi đè toán tử invoke để lớp này có thể được gọi như một hàm.
     * Ví dụ: getChargePointsUseCase() thay vì getChargePointsUseCase.execute()
     */
    suspend operator fun invoke(): List<ChargePoint> {
        // Logic nghiệp vụ có thể được thêm vào đây.
        // Ví dụ: lọc, sắp xếp, hoặc kết hợp dữ liệu từ nhiều nguồn.
        // Hiện tại, chúng ta chỉ cần gọi thẳng đến repository.
        return repository.getAllChargePoints()
    }
}
    