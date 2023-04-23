package com.example.bookingapp.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val id: String,
    val userId: String,
    val establishmentId: String,
    val dateOfCreation: Timestamp,
    val rate: Float,
    val comment: String
) {
    constructor() : this("Unknown", "Unknown", Timestamp(0, 0), 0F, "")
    constructor(
        userId: String,
        establishmentId: String,
        dateOfCreation: Timestamp,
        rate: Float,
        comment: String
    ) : this(
        "", userId, establishmentId, dateOfCreation, rate, comment
    )

}