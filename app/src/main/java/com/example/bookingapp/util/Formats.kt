package com.example.bookingapp.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(date: Date): String {
    return SimpleDateFormat(
        "d MMMM, yyyy, h:mm a",
        Locale.getDefault()
    ).format(date.time)
}