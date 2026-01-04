package com.quest.evrouting.data.simulation

import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Utility {

    // ------ Dùng chung ------

    fun getRandomDuration(minDurationMillis: Long, maxDurationMillis: Long): Long {
        require(minDurationMillis < maxDurationMillis) { "Thời gian tối thiểu phải nhỏ hơn tối đa." }
        return Random.nextLong(minDurationMillis, maxDurationMillis)
    }


    // ------ Dùng cho xe ------

    // Quyết định thời gian giữa 2 đợt sạc dựa trên khung giờ
    enum class TimeInterval(val minMillis: Long, val maxMillis: Long) {
        PRE_SLEEP_PEAK(TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(15)), // Giờ cao điểm "trước khi ngủ": 5-15 phút có 1 đợt xe
        DEEP_SLEEP(TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(4)),     // Giờ ngủ sâu: 2-4 giờ có 1 đợt xe
        NORMAL(TimeUnit.MINUTES.toMillis(30), TimeUnit.HOURS.toMillis(1)),      // Giờ bình thường: 30-60 phút có 1 đợt xe
        OFF_PEAK(TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(3))        // Giờ thấp điểm: 1-3 giờ có 1 đợt xe
    }

    // Hàm quyết định khung giờ dựa trên thời gian hiện tại
    fun getTimeInterval(currentTimeMillis: Long): TimeInterval {
        val currentHourOfDay = (currentTimeMillis / (1000 * 60 * 60)) % 24
        return when (currentHourOfDay.toInt()) {
            in 21..23 -> TimeInterval.PRE_SLEEP_PEAK
            in 0 .. 7 -> TimeInterval.DEEP_SLEEP
            in 9 .. 12, in 13 .. 17 -> TimeInterval.NORMAL
            else -> TimeInterval.OFF_PEAK
        }
    }

    // Hàm quyết định số xe theo các khung giờ
    fun determineGroupSize(interval: TimeInterval, totalConnections: Int): Int {
        val basePercentage = when (interval) {
            TimeInterval.PRE_SLEEP_PEAK -> 0.85 // Số lượng xe 1 đợt = 85% tổng số connection
            TimeInterval.DEEP_SLEEP -> 0.03     // Số lượng xe 1 đợt = 3% tổng số connection
            TimeInterval.NORMAL -> 0.35         // Số lượng xe 1 đợt = 35% tổng số connection
            TimeInterval.OFF_PEAK -> 0.1        // Số lượng xe 1 đợt = 10% tổng số connection
        }
        val averageCarCount = (totalConnections * basePercentage).toInt()
        // Sinh khoảng để đa dạng dữ liệu chênh lệch (min: 80%, max: 120%)
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