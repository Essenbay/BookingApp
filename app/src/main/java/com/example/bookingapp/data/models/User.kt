package com.example.bookingapp.data.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String,
    val fullName: String,
    val phoneNumber: String,
    val reservationIds: List<String>
) {
    @Suppress("unused")
    constructor() : this("", "", "", emptyList())
}