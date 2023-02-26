package com.example.bookingapp.data.firestore.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String,
    val firstname: String,
    val lastname: String
) {
    @Suppress("unused")
    constructor() : this("", "", "")
}