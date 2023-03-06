package com.example.bookingapp.data.models

import com.google.firebase.firestore.DocumentId

//Todo: Add places list
data class Establishment(
    @DocumentId
    val establishmentId: String,
    val name: String,
    val description: String,
    val address: String,
    val workingTime: String,
    val phoneNumbers: List<String>,
) {
    @Suppress("unused")
    constructor() : this("", "", "", "", "", listOf())
    constructor(
        name: String,
        description: String,
        address: String,
        workingTime: String,
        phoneNumbers: List<String>
    ) : this("", name, description, address, workingTime, phoneNumbers)
}