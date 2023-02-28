package com.example.bookingapp.data.sources

import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.models.User
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreSource private constructor(){
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun createUser(
        firebaseUserUid: String,
        userFirstname: String,
        userLastname: String
    ) {
        val newUser = User(firebaseUserUid, userFirstname, userLastname)
        db.collection("users").document(newUser.uid).set(newUser)
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

    companion object {
        private var INSTANCE: FirestoreSource? = null

        fun get(): FirestoreSource {
            if (INSTANCE == null) INSTANCE = FirestoreSource()
            return INSTANCE ?: throw IllegalArgumentException("FirestoreSource must be initialized")
        }
    }
}