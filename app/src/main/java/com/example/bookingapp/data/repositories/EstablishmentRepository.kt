package com.example.bookingapp.data.repositories

import androidx.lifecycle.viewModelScope
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource
import com.example.bookingapp.util.FirebaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EstablishmentRepository(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSource
) {

}