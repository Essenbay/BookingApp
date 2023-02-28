package com.example.bookingapp.data.sources

import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.models.User
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirestoreSource {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun createUser(
        firebaseUserUid: String,
        userFirstname: String,
        userLastname: String
    ): FirebaseResult<Boolean> = suspendCoroutine { cont ->
        val newUser = User(firebaseUserUid, userFirstname, userLastname)
        db.collection("users").document(newUser.uid)
            .set(newUser).addOnSuccessListener {
                cont.resume(FirebaseResult.Success(true))
            }.addOnFailureListener {
                cont.resume(FirebaseResult.Error(it))
            }
    }

    suspend fun getUser(): FirebaseResult<User> {
        TODO("Not yet implemented")
    }


    suspend fun addUser(user: User) {
        db.collection("users").add(user)
    }

    suspend fun getReservations(userID: String): FirebaseResult<List<Reservation>> {
        TODO("Not yet implemented")
    }
}