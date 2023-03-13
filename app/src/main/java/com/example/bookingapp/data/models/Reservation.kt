package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String,
    val userID: String,
    val establishment: Establishment,
    val tableID: Int,
    val dateTime: Timestamp
) {
    @Suppress("unused")
    constructor() : this(
        "", "", Establishment(), 0,
        Timestamp(0, 0)
    )

    constructor(
        userID: String,
        establishment: Establishment,
        tableID: Int,
        dateTime: Timestamp
    ) : this(
        "",
        userID,
        establishment,
        tableID,
        dateTime
    )
}