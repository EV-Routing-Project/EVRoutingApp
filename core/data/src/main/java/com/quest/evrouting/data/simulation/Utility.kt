package com.quest.evrouting.data.simulation

import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Utility {

    private const val MORNING_PEAK_START = 7  // 7h sáng
    private const val MORNING_PEAK_END = 10   // 10h trưa
    private const val EVENING_PEAK_START = 17 // 17h chiều
    private const val EVENING_PEAK_END = 20   // 20h tối

    // ------ Dùng chung ------

    fun getRandomDuration(minDurationMillis: Long, maxDurationMillis: Long): Long {
        require(minDurationMillis < maxDurationMillis) { "Thời gian tối thiểu phải nhỏ hơn tối đa." }
        return Random.nextLong(minDurationMillis, maxDurationMillis)
    }


    // ------ Dùng cho xe ------

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
    fun determineGroupSize(interval: TimeInterval, totalConnections: Int): Int {
        val basePercentage = when (interval) {
            TimeInterval.PEAK -> 0.4        // 40%
            TimeInterval.NORMAL -> 0.22     // 22%
            TimeInterval.OFF_PEAK -> 0.08   // 8%
        }
        val averageCarCount = (totalConnections * basePercentage).toInt()
        // Sinh khoảng để đa dạng dữ liệu
        val minCount = (averageCarCount * 0.8).toInt().coerceAtLeast(1)
        val maxCount = (averageCarCount * 1.2).toInt().coerceAtLeast(minCount)
        return Random.nextInt(minCount, maxCount + 1)
    }


    // ------ Dùng cho Connection Sim ------

    // Quyết định các sự kiện có xảy ra hay không
    fun shouldEventOccur(percentage: Int): Boolean {
        if (percentage <= 0) return false
        if (percentage >= 100) return true
        return Random.nextInt(1, 101) <= percentage
    }

}