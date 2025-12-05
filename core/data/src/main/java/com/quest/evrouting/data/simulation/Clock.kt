package com.quest.evrouting.data.simulation

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

// object này dùng để test thời gian thực hiện tác vụ
object Clock {
    private val startTimeMillis = AtomicLong(0L)

    @Volatile
    private var timeMultiplier: Double = 1.0

    fun start() {
        // System.currentTimeMillis() trả về thời gian bằng ms tính từ năm 1970
        startTimeMillis.set(System.currentTimeMillis())
        println("🕰️ Clock đã bắt đầu. Thời gian mô phỏng T=0.")
    }

    fun getCurrentTimestamp(): Long {
        val realElapsedTime = System.currentTimeMillis() - startTimeMillis.get()
        return (realElapsedTime * timeMultiplier).toLong()
    }

    fun setMultiplier(multiplier: Double) {
        require(multiplier > 0) { "Hệ số tăng tốc (multiplier) phải là một số dương." }
        println("⚙️ Tốc độ đồng hồ đã thay đổi từ $timeMultiplier sang $multiplier.")
        this.timeMultiplier = multiplier
    }

    fun getMultiplier(): Double = this.timeMultiplier

    inline fun measureRealTime(taskName: String, block: () -> Unit){
        val duration = measureTimeMillis {
            block()
        }
        println("⏱️ Tác vụ '$taskName' mất $duration ms thời gian thực để hoàn thành.")
    }
}


fun main() = runBlocking {

    // 1. Bắt đầu đồng hồ mô phỏng
    Clock.start()

    // 2. Thiết lập tốc độ (ví dụ: nhanh gấp 50 lần)
    Clock.setMultiplier(50.0)

    // Lấy thời gian hiện tại của mô phỏng
    val timestamp1 = Clock.getCurrentTimestamp()
    println("Timestamp mô phỏng đầu tiên: $timestamp1 ms")

    // Giả vờ một tác vụ đang chạy trong 2 giây thực tế
    delay(2000)

    // Lấy lại thời gian mô phỏng
    val timestamp2 = Clock.getCurrentTimestamp()
    val simulatedElapsedTime = timestamp2 - timestamp1

    println("Timestamp mô phỏng sau đó: $timestamp2 ms")
    println("-> Đã trôi qua ~2000 ms thời gian thực.")
    println("-> Tương đương $simulatedElapsedTime ms (${simulatedElapsedTime / 1000} giây) trong mô phỏng.")

}