package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReservationsViewModel(
    private val reservationRepository: ReceiveReservations,
    private val userRepository: AccessUser
) : ViewModel() {
    private var _reservationEstablishmentMap: Map<Reservation, Establishment> = emptyMap()
    private var _filteredReservations: MutableStateFlow<SearchResult<Map<Reservation, Establishment>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredReservations = _filteredReservations.asStateFlow()
    val user: StateFlow<FirebaseUser?> = userRepository.user

    init {
        viewModelScope.launch {
            user.collect {
                if (it != null) {
                    getReservations()
                }
            }
        }
    }

    fun getReservations() = viewModelScope.launch {
        if(user.value != null) {
            val result = reservationRepository.getReservationEstablishmentMapByUser(user.value!!.uid)
            _reservationEstablishmentMap = result
            searchReservations("")
        } else {
            _filteredReservations.update {
                SearchResult.Error(UserNotSignedIn())
            }
        }
    }


    suspend fun searchReservations(query: String?) {
        _filteredReservations.update { startSearchReservations(query) }
    }

    private suspend fun startSearchReservations(query: String?): SearchResult<Map<Reservation, Establishment>> =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank())
                cont.resume(SearchResult.Success(_reservationEstablishmentMap))
            else {
                val resultList:MutableMap<Reservation, Establishment> = mutableMapOf()
                for (pair in _reservationEstablishmentMap) {
                    if (pair.value.name.lowercase().contains(query.lowercase())) resultList[pair.key] =
                        pair.value
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))
            }
        }

    suspend fun getEstablishmentById(establishmentId: String): Establishment =
        reservationRepository.getEstablishmentById(establishmentId)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firestoreRepository = application.container.storageRepository
                val firebaseUserID = application.container.userRepository
                ReservationsViewModel(firestoreRepository, firebaseUserID)
            }
        }
    }
}