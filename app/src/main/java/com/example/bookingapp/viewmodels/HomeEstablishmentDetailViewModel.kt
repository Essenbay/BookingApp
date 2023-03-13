package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class HomeEstablishmentDetailViewModel(
    private val establishmentID: String,
    private val establishmentRepository: EstablishmentRepository,
    private val accessUser: AccessUser
) : ViewModel() {
    private val _establishment: MutableStateFlow<FirebaseResult<Establishment>?> =
        MutableStateFlow(null)
    val establishment: StateFlow<FirebaseResult<Establishment>?> =
        _establishment.asStateFlow()

    init {
        viewModelScope.launch {
            _establishment.update { establishmentRepository.getEstablishment(establishmentID) }
        }
    }

    suspend fun createReservation(
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ): FirebaseResult<Boolean> {
        val userUID = accessUser.user.value?.uid
        return if (userUID == null) FirebaseResult.Error(UserNotSignedIn())
        else {
            establishmentRepository.createReservation(
                userUID,
                establishment,
                tableID,
                date
            )
        }
    }

    companion object {
        fun getFactory(establishmentID: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication
                val firestoreRepository = application.container.storageRepository
                val accessUserIDRepository = application.container.userRepository
                HomeEstablishmentDetailViewModel(
                    establishmentID,
                    firestoreRepository,
                    accessUserIDRepository
                )
            }
        }
    }
}




