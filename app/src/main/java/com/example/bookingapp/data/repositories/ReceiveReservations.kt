package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.util.FirebaseResult

interface ReceiveReservations {
    suspend fun getReservationHistory(): FirebaseResult<List<Reservation>>
}