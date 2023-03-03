package com.example.bookingapp.data.models

import com.google.firebase.firestore.DocumentId

//Todo: Add places list
data class Establishment(
    @DocumentId
    val establishmentId: String,
    val name: String
) {
    @Suppress("unused")
    constructor() : this("", "")
    constructor(name: String) : this("", name)
}