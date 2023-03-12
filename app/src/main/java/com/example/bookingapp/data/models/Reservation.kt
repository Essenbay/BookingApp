package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String,
    val userID: String,
    val establishmentId: String,
    val tableID: Int,
    val dateTime: Timestamp
) {
    @Suppress("unused")
    constructor() : this(
        "", "", "", 0,
        Timestamp(0, 0)
    )

    constructor(
        establishmentId: String,
        userID: String,
        tableID: Int,
        dateTime: Timestamp
    ) : this(
        "",
        establishmentId,
        userID,
        tableID,
        dateTime
    )
}