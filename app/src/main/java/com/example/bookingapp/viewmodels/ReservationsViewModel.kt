package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.ReservationWithEstablishment
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
    private var _reservationEstablishments: List<ReservationWithEstablishment> = listOf()
    private var _filteredReservations: MutableStateFlow<SearchResult<List<ReservationWithEstablishment>>> =
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
            val result = reservationRepository.getReservationEstablishmentByUser(user.value!!.uid)
            _reservationEstablishments = result
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

    private suspend fun startSearchReservations(query: String?): SearchResult<List<ReservationWithEstablishment>> =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank())
                cont.resume(SearchResult.Success(_reservationEstablishments))
            else {
                val resultList:MutableList<ReservationWithEstablishment> = mutableListOf()
                for (pair in _reservationEstablishments) {
                    if (pair.establishment.name.lowercase().contains(query.lowercase())) resultList.add(pair)
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))
            }
        }

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