package com.example.bookingapp.data.datasource

import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val USER_COLLECTION = "users"
const val RESERVATION_COLLECTION = "reservations"
const val ESTABLISHMENT_COLLECTION = "establishments"

class FirestoreRepository : ReceiveReservations, EstablishmentsRepository {
    private val db: FirebaseFirestore = Firebase.firestore


    override suspend fun getEstablishments(): FirebaseResult<List<Establishment>> =
        suspendCoroutine { cont ->
            val snapshot = db.collection(ESTABLISHMENT_COLLECTION).get()
            val establishments: MutableList<Establishment> = mutableListOf()
            snapshot
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val establishment = document.toObject(Establishment::class.java)
                        establishments.add(establishment)
                    }
                    cont.resume(FirebaseResult.Success(establishments))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    override suspend fun getEstablishment(id: String): FirebaseResult<Establishment> =
        suspendCoroutine { cont ->
            val snapshot = db.collection(ESTABLISHMENT_COLLECTION).document(id).get()
            snapshot
                .addOnSuccessListener {
                    val establishment = it.toObject(Establishment::class.java)
                    if (establishment == null) cont.resume(FirebaseResult.Error(Exception("Couldn't not convert establishment")))
                    else cont.resume(FirebaseResult.Success(establishment))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    override suspend fun createReservation(reservation: Reservation): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            db.collection(RESERVATION_COLLECTION).document().set(reservation)
                .addOnSuccessListener {
                    cont.resume(FirebaseResult.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    //Find list of reservations in certain establishment then check if there is with same table and date/time
    private suspend fun checkIfReservationReserved(reservation: Reservation): FirebaseResult<Boolean> {
        when (val resultRes = getReservationsByEstablishment(reservation.establishmentId)) {
            is FirebaseResult.Success -> {
                for (res in resultRes.data) {
                    if (res.dateTime == reservation.dateTime && res.tableID == reservation.tableID) {
                        return FirebaseResult.Success(false)
                    }
                }
                return FirebaseResult.Success(true)
            }
            is FirebaseResult.Error -> return FirebaseResult.Error(resultRes.exception)
        }
    }

    override suspend fun getReservationsByEstablishment(establishmentID: String): FirebaseResult<List<Reservation>> =
        suspendCoroutine { cont ->
            val snapshot = db.collection(RESERVATION_COLLECTION)
                .whereEqualTo("establishmentId", establishmentID).get()
            val reservations: MutableList<Reservation> = mutableListOf()
            snapshot
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val reservation = document.toObject(Reservation::class.java)
                        reservations.add(reservation)
                    }
                    cont.resume(FirebaseResult.Success(reservations))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    override suspend fun createEstablishment(establishment: Establishment): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            db.collection(ESTABLISHMENT_COLLECTION).document().set(establishment)
                .addOnSuccessListener {
                    cont.resume(FirebaseResult.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }


    override suspend fun getReservationHistory(): FirebaseResult<List<Reservation>> =
        suspendCoroutine { cont ->
            val snapshot = db.collection(RESERVATION_COLLECTION).get()
            val reservations: MutableList<Reservation> = mutableListOf()
            snapshot
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val reservation = document.toObject(Reservation::class.java)
                        reservations.add(reservation)
                    }
                    cont.resume(FirebaseResult.Success(reservations))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

}