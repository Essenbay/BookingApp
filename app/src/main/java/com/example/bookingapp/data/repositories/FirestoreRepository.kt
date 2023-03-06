package com.example.bookingapp.data.sources

import android.util.Log
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.models.User
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val USER_COLLECTION = "users"
const val RESERVATION_COLLECTION = "reservations"
const val ESTABLISHMENT_COLLECTION = "establishments"


class FirestoreSource {
    private val db: FirebaseFirestore = Firebase.firestore


    suspend fun createUser(
        firebaseUserUid: String,
        fullName: String,
        phoneNumber: String
    ): FirebaseResult<User> = suspendCoroutine { cont ->
        val newUser = User(firebaseUserUid, fullName, phoneNumber, emptyList())
        db.collection(USER_COLLECTION).document(newUser.uid)
            .set(newUser).addOnSuccessListener {
                cont.resume(FirebaseResult.Success(newUser))
            }.addOnFailureListener {
                cont.resume(FirebaseResult.Error(it))
            }
    }

    suspend fun getUser(userID: String): User = suspendCoroutine { cont ->
        val docRef = db.collection(USER_COLLECTION).document(userID)
        docRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    val resultUser = it.toObject(User::class.java)
                    Log.d("FirestoreSource", resultUser.toString())
                    if (resultUser == null) {
                        cont.resume(
                            throw Exception("Can not create user object")
                        )
                    } else {
                        cont.resume(resultUser)
                    }
                }
            }.addOnFailureListener {
                FirebaseResult.Error(it)
            }
    }

//    suspend fun deleteUser(firebaseUserUid: String): FirebaseResult<Boolean> =
//        suspendCoroutine { cont ->
//            db.collection(USER_COLLECTION).document(firebaseUserUid).delete()
//                .addOnSuccessListener {
//                    cont.resume(FirebaseResult.Success(true))
//                }.addOnFailureListener {
//                    cont.resume(FirebaseResult.Error(it))
//                }
//            //Todo: deleting user does not delete its reservations
//        }

    suspend fun getReservations(userID: String): FirebaseResult<List<Reservation>> {
        TODO("Not yet implemented")
    }

    suspend fun addEstablishment(
        name: String,
        description: String,
        address: String,
        workingTime: String,
        phoneNumbers: List<String>
    ): FirebaseResult<Boolean> = suspendCoroutine { cont ->
        val newEstablishment = Establishment(name, description, address, workingTime, phoneNumbers)
        val result = db.collection(ESTABLISHMENT_COLLECTION).document().set(newEstablishment)
        result.addOnSuccessListener {
            cont.resume(FirebaseResult.Success(true))
        }
            .addOnFailureListener {
                cont.resume(FirebaseResult.Error(it))
            }
    }

    //Working with establishments

    suspend fun getEstablishments(): FirebaseResult<List<Establishment>> =
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

}