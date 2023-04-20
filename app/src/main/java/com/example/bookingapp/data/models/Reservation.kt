package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String,
    val userID: String,
    val establishmentId: String,
    val tableID: Int,
    val fromDate: Timestamp,
    val toDate: Timestamp
) {
    @Suppress("unused")
    constructor() : this(
        "", "Unknown", "Unknown", 0,
        Timestamp(0, 0), Timestamp(0, 0)
    )

    constructor(
        userID: String,
        establishmentId: String,
        tableID: Int,
        fromDate: Timestamp,
        toDate: Timestamp
    ) : this(
        "",
        userID,
        establishmentId,
        tableID,
        fromDate, toDate
    )
}