package com.example.bookingapp.data.datasource

import com.example.bookingapp.data.models.*
import com.example.bookingapp.data.repositories.ReviewRepository
import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.CommentNotFound
import com.example.bookingapp.util.EstablishmentNotFound
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.ReservationIsNotFree
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val USER_COLLECTION = "users"
const val RESERVATION_COLLECTION = "reservations"
const val ESTABLISHMENT_COLLECTION = "establishments"
const val COMMENTS_COLLECTION = "comments"

class FirestoreRepository @Inject constructor() : ReceiveReservations, EstablishmentsRepository, EstablishmentRepository,
    ReviewRepository {
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

    override suspend fun createReservation(
        userUID: String,
        establishmentId: String,
        tableID: Int,
        fromDate: Timestamp,
        toDate: Timestamp
    ): FirebaseResult<Boolean> {
        return when (val checkResult =
            checkIfReservationReserved(establishmentId, tableID, fromDate, toDate)) {
            is FirebaseResult.Success -> {
                suspendCoroutine { cont ->
                    val newReservation =
                        Reservation(userUID, establishmentId, tableID, fromDate, toDate)
                    db.collection(RESERVATION_COLLECTION).document().set(newReservation)
                        .addOnSuccessListener {
                            cont.resume(FirebaseResult.Success(true))
                        }
                        .addOnFailureListener {
                            cont.resume(FirebaseResult.Error(it))
                        }
                }
            }
            is FirebaseResult.Error -> {
                checkResult
            }
        }
    }

    //Find list of reservations in certain establishment then check if there is with same table and date/time
    private suspend fun checkIfReservationReserved(
        establishmentId: String,
        tableID: Int,
        fromDate: Timestamp,
        toDate: Timestamp
    ): FirebaseResult<Boolean> {
        when (val resultRes = getReservationsByEstablishment(establishmentId)) {
            is FirebaseResult.Success -> {
                for (res in resultRes.data) {
                    if (res.fromDate.toDate().before(fromDate.toDate()) && res.toDate.toDate()
                            .after(toDate.toDate()) && res.tableID == tableID
                    ) {
                        return FirebaseResult.Error(ReservationIsNotFree())
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


    override suspend fun getReservationEstablishmentByUser(userID: String): List<ReservationWithEstablishment> {
        val reservations =
            db.collection(RESERVATION_COLLECTION).whereEqualTo("userID", userID).get()
                .await().documents.map {
                    val r = it.toObject(Reservation::class.java)
                    if (r == null) {
                        return@map Reservation()
                    } else return@map r
                }

        val establishmentIds = reservations.map { it.establishmentId }.distinct()
        val establishments = establishmentIds.map { establishmentId ->
            db.collection(ESTABLISHMENT_COLLECTION).document(establishmentId).get().await()
                .toObject(Establishment::class.java)
        }
        val establishmentsMap = establishments.associateBy { it?.establishmentId ?: "" }
        val resultList: MutableList<ReservationWithEstablishment> = mutableListOf()
        reservations.forEach { reservation ->
            resultList.add(
                ReservationWithEstablishment(
                    reservation,
                    establishmentsMap[reservation.establishmentId] ?: Establishment()
                )
            )
        }
        return resultList
    }

    override suspend fun getEstablishmentById(establishmentId: String): Establishment =
        suspendCoroutine { cont ->
            val snapshot =
                db.collection(ESTABLISHMENT_COLLECTION).document(establishmentId).get()
            snapshot
                .addOnSuccessListener { result ->
                    val establishment = result.toObject(Establishment::class.java)
                    if (establishment != null) {
                        cont.resume(establishment)
                    } else {
                        throw EstablishmentNotFound()
                    }
                }
                .addOnFailureListener {
                    throw EstablishmentNotFound()
                }

        }

    override suspend fun getReviewsByEstablishment(establishmentId: String): List<ReviewUI> {
        val reviews =
            db.collection(COMMENTS_COLLECTION).whereEqualTo("establishmentId", establishmentId)
                .get().await().documents.map {
                    val r = it.toObject(Review::class.java)
                    if (r == null) {
                        return@map Review()
                    } else return@map r
                }
        val userIds = reviews.map { it.userId }.distinct()
        val users = userIds.map { userId ->
            getUserById(userId)
        }
        val usersMap = users.associateBy { it.uid }
        val resultList: MutableList<ReviewUI> = mutableListOf()
        reviews.forEach { review ->
            resultList.add(
                ReviewUI(
                    review,
                    usersMap[review.userId]?.fullName ?: "Unknown"
                )
            )
        }
        return resultList
    }

    suspend fun getUserById(userId: String): User =
        db.collection(USER_COLLECTION).document(userId).get().await()
            .toObject(User::class.java) ?: User()

    override suspend fun createReview(
        userId: String,
        establishmentId: String,
        dateOfCreation: Timestamp,
        rate: Float,
        comment: String
    ): Boolean = suspendCoroutine { cont ->
        val newReview = Review(userId, establishmentId, dateOfCreation, rate, comment)
        val snapshot =
            db.collection(COMMENTS_COLLECTION).document().set(newReview)
        snapshot
            .addOnSuccessListener {

                cont.resume(true)
            }
            .addOnFailureListener {
                throw EstablishmentNotFound()
            }

    }
}