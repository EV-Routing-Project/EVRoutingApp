package com.quest.evrouting.algorithm.infrastructure.adapter.tool.code

import com.quest.evrouting.algorithm.domain.model.Geohash
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.code.GeohashProviderPort
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@RunWith(Parameterized::class)
class GeohashProviderAdapterTest(
    private val testcaseName: String,
    private val significantBits: Int,
    private val point: Point,
) {
    private lateinit var geohashProvider: GeohashProviderPort

    @Before
    fun setUp(){
        geohashProvider = GeohashProviderAdapter()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun parameters(): Collection<Array<Any>>{
            val list = mutableListOf<Array<Any>>()
            list.add(arrayOf("Testcase 0", 64, Point(0.0,0.0)))
            val numOfTest = 100
            for(i in 1..numOfTest){
                val lon = Random.nextDouble(-180.0, 180.0)
                val lat = Random.nextDouble(-90.0, 90.0)
                val point = Point(lon, lat)
                val significantBits = Random.nextInt(1, 64)
                list.add(arrayOf("Testcase $i", significantBits, point))
            }
            return list
        }
    }

    fun encode(point: Point, significantBits: Int): Long {
        val location = arrayOf(point.lon, point.lat)
        val ranges = arrayOf(arrayOf(-180.0, 180.0), arrayOf(-90.0, 90.0))
        var choose = 0
        var geohash: Long = 0
        for(i in 1..significantBits){
            val range = ranges[choose]
            val mid = (range[0] + range[1]) / 2
            geohash = geohash shl 1
            if(location[choose] >= mid){
                geohash = geohash or 1
                range[0] = mid
            } else {
                geohash = geohash or 0
                range[1] = mid
            }
            choose = 1 - choose
        }
        return geohash.shl(64 - significantBits)
    }

    fun toBinaryString(value: Long): String {
        val binaryString = StringBuilder()
        for(i in 63 downTo 0){
            val bit = (value shr i) and 1
            binaryString.append(bit)
        }
        return binaryString.toString()
    }

    @Test
    fun testEncode() {
        val result = encode(point, significantBits)
        var geohash: Geohash
        val excuseTime = measureTimeMillis {
            geohash = geohashProvider.encode(point, significantBits)
        }
        Assert.assertEquals(result, geohash.value)
        println("--- [$testcaseName] Đang chạy testEncode ---")
        println("Point((${point.lon},${point.lat})) - sign = $significantBits")
        println("Thời gian chạy: $excuseTime ms")
        println("- Geohash:")
        println("\tnumber value = ${geohash.value}")
        println("\tbit value = ${toBinaryString(geohash.value)}")
        println("- Result:")
        println("\tnumber value = ${result}")
        println("\tbit value = ${toBinaryString(result)}")
        println("- Equals: ${geohash.value == result}")
        println("Done Test!\n")
    }

    @Test
    fun testGetGeohashGridForPoint() {
        var result: List<Geohash>
        val geohash = encode(point, significantBits)
        val excuseTime = measureTimeMillis {
            result = geohashProvider.getGeohashGridForPoint(point, significantBits)
        }
        println("--- [$testcaseName] Đang chạy testEncode ---")
        println("Point((${point.lon},${point.lat})) - sign = $significantBits")
        println("geohash: ${toBinaryString(encode(point, significantBits))}")
        println("geohash: ${toBinaryString(encode(point, 64))}")
        println("Thời gian chạy: $excuseTime ms")
        for((idx,item) in result.withIndex()){
            println("- Geohash Adj ${idx + 1}:")
            println("\tnumber value = ${item.value}")
            println("\tbit value = ${toBinaryString(item.value)}")
        }
        println("Done Test!\n")
    }
}