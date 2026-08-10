package com.rayara.sevasetu.data.models

enum class PaymentMode(val displayName: String, val kannadaName: String) {
    CASH("Cash", "ನಗದು"),
    UPI("UPI", "UPI"),
    ONLINE("Online", "ಆನ್‌ಲೈನ್");

    companion object {
        fun fromString(value: String): PaymentMode {
            return values().find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) ||
                // Backward compatibility for old PhonePe entries
                (it == UPI && value.equals("PHONEPE", ignoreCase = true))
            } ?: CASH
        }
    }
}
