package com.quest.evrounting.data.simulation

import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Utility {

    private const val MORNING_PEAK_START = 7  // 7h sáng
    private const val MORNING_PEAK_END = 10   // 10h trưa
    private const val EVENING_PEAK_START = 17 // 17h chiều
    private const val EVENING_PEAK_END = 20   // 20h tối


    // Định nghĩa thời gian chờ dựa trên khung giờ
    enum class TimeInterval(val minMillis: Long, val maxMillis: Long) {
        PEAK(TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(20)),      // Giờ cao điểm: 5-20 phút có 1 đợt xe
        NORMAL(TimeUnit.MINUTES.toMillis(20), TimeUnit.HOURS.toMillis(1)),      // Giờ bình thường: 20-60 phút có 1 đợt xe
        OFF_PEAK(TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(4))         // Giờ thấp điểm: 1-4 giờ có 1 đợt xe
    }

    // Hàm quyết định khung giờ dựa trên thời gian hiện tại
    fun getTimeInterval(currentTimeMillis: Long): TimeInterval {
        val currentHourOfDay = (currentTimeMillis / (1000 * 60 * 60)) % 24
        return when (currentHourOfDay.toInt()) {
            in MORNING_PEAK_START until MORNING_PEAK_END -> TimeInterval.PEAK
            in EVENING_PEAK_START until EVENING_PEAK_END -> TimeInterval.PEAK
            in 0..6, in 22..23 -> TimeInterval.OFF_PEAK // Nửa đêm về sáng
            else -> TimeInterval.NORMAL
        }
    }

    // Hàm quyết định số xe theo các khung giờ
    fun determineGroupSize(interval: TimeInterval): Int {
        val randomValue = Random.nextInt(1, 101) // Sinh số từ 1 đến 100
        return when (interval) {
            TimeInterval.PEAK -> when {
                // when hoạt động như if-else nếu không có tham số truyền vào
                randomValue <= 50 -> 1 // randomValue từ 1 đến 50: 50%
                randomValue <= 85 -> 2 // randomValue từ 51 đến 85: 35%
                else -> 3              // randomValue từ 86 đến 100: 15%
            }
            TimeInterval.NORMAL -> when {
                randomValue <= 80 -> 1 // 80%
                else -> 2              // 20%
            }
            TimeInterval.OFF_PEAK -> 1 // Giờ thấp điểm luôn luôn chỉ có 1 xe
        }
    }
}