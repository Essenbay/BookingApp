package com.example.bookingapp.util

import android.util.Patterns

fun checkLoginField(emailInput: String): Boolean {
    var isValid = true
    val matcher = Patterns.EMAIL_ADDRESS.matcher(emailInput)
    if (!matcher.matches()) {
        isValid = false
    }

    return isValid
}

fun checkPhoneNumberField(phoneNumberInput: String): Boolean {
    var isValid = true
    val matcher = Patterns.PHONE.matcher(phoneNumberInput)
    if (!matcher.matches()) {
        isValid = false
    }

    return isValid
}

fun checkPasswordField(passwordInput: String): Boolean {
    return passwordInput.length >= 6
}

fun checkPasswordMatching(firstPass: String, secondPass: String): Boolean {
    return firstPass == secondPass
}