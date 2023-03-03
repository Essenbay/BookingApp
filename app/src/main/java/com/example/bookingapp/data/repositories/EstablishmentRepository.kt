package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.sources.FirestoreSource
import com.example.bookingapp.util.FirebaseResult

class EstablishmentRepository(
    private val firebaseRepository: FirebaseUserRepository,
    private val firestoreSource: FirestoreSource
) {

    suspend fun getEstablishments(): FirebaseResult<List<Establishment>> = firestoreSource.getEstablishments()
}