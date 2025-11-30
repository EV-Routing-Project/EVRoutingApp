package com.quest.evrounting.data.simulation

import kotlin.math.ln
import kotlin.math.pow

// Xu hướng (Strategy)
enum class ChargingStrategy {
    POWER_FOCUSED,
    BALANCED,
    LOCATION_FOCUSED
}

//  Công thức (Formula)
fun interface ScoreFormula {
    fun calculate(powerKw: Double, popularityScore: Int, profile: ChargingStrategy): Double
}

object TieredFormula : ScoreFormula {
    override fun calculate(powerKw: Double, popularityScore: Int, profile: ChargingStrategy): Double {
        val powerComponent = when (profile) {
            ChargingStrategy.POWER_FOCUSED -> when {
                powerKw >= 150.0 -> 150.0
                powerKw >= 75.0  -> 100.0
                powerKw >= 43.0  -> 60.0
                powerKw >= 22.0  -> 30.0
                else             -> 10.0
            }
            ChargingStrategy.BALANCED -> when {
                powerKw >= 150.0 -> 120.0
                powerKw >= 75.0  -> 90.0
                powerKw >= 43.0  -> 50.0
                powerKw >= 22.0  -> 20.0
                else             -> 5.0
            }
            ChargingStrategy.LOCATION_FOCUSED -> when {
                powerKw >= 50.0 -> 40.0
                else            -> 20.0
            }
        }
        val popularityComponent = popularityScore * 10.0
        return (powerComponent + popularityComponent)
    }
}

object LogPowFormula : ScoreFormula {
    override fun calculate(powerKw: Double, popularityScore: Int, profile: ChargingStrategy): Double {
        val (powerMultiplier, popularityExponent) = when (profile) {
            ChargingStrategy.POWER_FOCUSED -> Pair(35.0, 1.5)
            ChargingStrategy.BALANCED -> Pair(25.0, 2.0)
            ChargingStrategy.LOCATION_FOCUSED -> Pair(15.0, 2.5)
        }

        val powerComponent = ln(powerKw.coerceAtLeast(1.0)) * powerMultiplier
        val popularityComponent = popularityScore.toDouble().pow(popularityExponent)
        return (powerComponent + popularityComponent)
    }
}

object CappedLinearFormula : ScoreFormula {
    override fun calculate(powerKw: Double, popularityScore: Int, profile: ChargingStrategy): Double {
        val (powerCap, popularityExponent) = when (profile) {
            ChargingStrategy.POWER_FOCUSED -> Pair(200.0, 1.8)
            ChargingStrategy.BALANCED -> Pair(180.0, 2.2)
            ChargingStrategy.LOCATION_FOCUSED -> Pair(120.0, 2.8)
        }

        val powerComponent = powerKw.coerceAtMost(powerCap)
        val popularityComponent = popularityScore.toDouble().pow(popularityExponent)
        return (powerComponent + popularityComponent)
    }
}

// Công thức sẽ quyết định sự khác biệt giữa các bộ dữ liệu (nên sẽ random theo từng đợt timestamp - nếu muốn mở rộng)
val allFormulas = listOf(TieredFormula, LogPowFormula, CappedLinearFormula)
// Xu hướng sẽ quyết định sự phân bố của 1 bộ dữ liệu (nên sẽ random theo từng TimeInterval trong ngày - nếu muốn mở rộng)
val allStrategy = ChargingStrategy.entries

