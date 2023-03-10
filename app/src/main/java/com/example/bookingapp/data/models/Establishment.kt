package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalTime

data class Establishment(
    @DocumentId
    val establishmentId: String,
    val name: String,
    val description: String,
    val address: String,
    val workingTimeStart: Timestamp,
    val workingTimeEnd: Timestamp,
    val phoneNumbers: String,
    val tableNumber: Int
) {
    @Suppress("unused")
    constructor() : this("", "", "", "", Timestamp(0, 0), Timestamp(0, 0), "", 0)
    constructor(
        name: String,
        description: String,
        address: String,
        workingTimeStart: Timestamp,
        workingTimeEnd: Timestamp,
        phoneNumber: String,
        tableNumber: Int
    ) : this(
        "",
        name,
        description,
        address,
        workingTimeStart,
        workingTimeEnd,
        phoneNumber,
        tableNumber
    )
}