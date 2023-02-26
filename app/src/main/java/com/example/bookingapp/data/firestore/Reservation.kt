package com.example.bookingapp.data.firestore

import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String,
    @DocumentId
    val establishmentId: String,
    @DocumentId
    val userID: String
) {
    @Suppress("unused")
    constructor() : this("", "", "")
}