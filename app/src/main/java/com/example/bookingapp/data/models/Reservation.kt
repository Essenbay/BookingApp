package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String,
    @DocumentId
    val userID: String,
    @DocumentId
    val establishmentId: String,
    val tableID: String,
    val dateTime: Timestamp
) {
    @Suppress("unused")
    constructor() : this(
        "", "", "", "",
        Timestamp(0, 0)
    )

    constructor(
        establishmentId: String,
        userID: String,
        tableID: String,
        dateTime: Timestamp
    ) : this(
        "",
        establishmentId,
        userID,
        tableID,
        dateTime
    )
}