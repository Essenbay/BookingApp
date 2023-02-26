package com.example.bookingapp.data.firestore

import com.example.bookingapp.data.firestore.models.Result
import com.example.bookingapp.data.firestore.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.suspendCoroutine
class FirestoreRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val storage = FirebaseStorage.getInstance().reference

    fun addUser(user: User) {
        db.collection("users").add(user)
    }

    fun editUser(userID: String) {
        TODO("Not yet implemented")
    }

    fun deleteUser(userID: String) {
        TODO("Not yet implemented")
    }


}