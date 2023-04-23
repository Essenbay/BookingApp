package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.ReservationWithEstablishment

interface ReceiveReservations {
    suspend fun getReservationEstablishmentByUser(userID: String): List<ReservationWithEstablishment>
    suspend fun getEstablishmentById(establishmentId: String): Establishment
}