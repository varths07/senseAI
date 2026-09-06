package com.example.senseai.data.model

enum class TrustLevel {
    HIGH,
    MEDIUM,
    LOW
}

enum class RiskLevel {
    HIGH,
    MEDIUM,
    LOW,
    SAFE
}

data class TrustResult(
    val trustScore: Float,
    val trustLevel: TrustLevel,
    val riskLevel: RiskLevel,
    val verificationCount: Int,
    val isVerified: Boolean
)
