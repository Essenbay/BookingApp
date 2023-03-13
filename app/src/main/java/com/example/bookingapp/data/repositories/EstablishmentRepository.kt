package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.Timestamp

interface EstablishmentRepository {
    suspend fun getEstablishment(id: String): FirebaseResult<Establishment>
    suspend fun createReservation(
        userUID: String,
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ): FirebaseResult<Boolean>
}
