package com.rayara.sevasetu.utils

object Constants {
    
    object Organization {
        const val NAME = "ಶ್ರೀ ರಾಘವೇಂದ್ರಸ್ವಾಮಿಗಳ ಬ್ರಂದಾವನ"
        const val ADDRESS = "ಭ್ರಾಹ್ಮಣರ ಬೀದಿ, ದೊಡ್ಡಬಳ್ಳಾಪುರ"
        const val TRUST = "ಶ್ರೀ ರಾಘವೇಂದ್ರ ಗುರುಸಾರ್ವಭೌಮ ಸೇವಾ ಟ್ರಸ್ಟ್ (ರಿ.)"
    }
    
    object ServiceAmounts {
        val PREDEFINED_AMOUNTS = listOf(100, 200, 500, 1000, 2000, 2500)
    }
    
    object Receipt {
        const val RECEIPT_PREFIX = "ಸಂ."
        const val DATE_PREFIX = "ದಿನಾಂಕ:"
        const val CUSTOMER_PREFIX = "ಶ್ರೀಮತಿ/ಶ್ರೀ:"
        const val PHONE_PREFIX = "ದೂರವಾಣಿ:"
        const val SERVICE_LABEL = "ಸೇವೆ"
        const val TOTAL_LABEL = "ಒಟ್ಟು:"
        const val PAYMENT_MODE_LABEL = "ಪಾವತಿ ವಿಧಾನ:"
        const val THANK_YOU = "ಧನ್ಯವಾದಗಳು"
    }
    
    object PDF {
        const val RECEIPT_FOLDER = "receipts"
        const val FILE_PREFIX = "receipt_"
        const val FILE_EXTENSION = ".pdf"
    }
    
    object Validation {
        const val MIN_PHONE_LENGTH = 10
        const val MAX_PHONE_LENGTH = 10
        const val MIN_NAME_LENGTH = 2
        const val MIN_AMOUNT = 1
        const val MANDATORY_DETAILS_THRESHOLD = 500
    }
    
    object DefaultValues {
        const val DEFAULT_CUSTOMER_NAME = "ಶ್ರೀ ರಾಯರ ಸೇವಾರ್ಥಿ"
        const val DEFAULT_PHONE_NUMBER = "-"
    }
}
