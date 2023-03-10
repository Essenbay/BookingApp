package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.util.FirebaseResult

interface EstablishmentsRepository {
    suspend fun getEstablishments(): FirebaseResult<List<Establishment>>
    suspend fun getEstablishment(id: String):FirebaseResult<Establishment>
    suspend fun createReservation(reservations: Reservation): FirebaseResult<Boolean>
    suspend fun getReservationsByEstablishment(establishmentID: String): FirebaseResult<List<Reservation>>
    suspend fun createEstablishment(establishment: Establishment):FirebaseResult<Boolean>
}