package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.util.FirebaseResult

interface ReceiveReservations {
    suspend fun getReservationEstablishmentMapByUser(userID: String): Map<Reservation, Establishment>

    suspend fun getEstablishmentById(establishmentId: String): Establishment
}