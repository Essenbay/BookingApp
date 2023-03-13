package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.SearchResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReservationsViewModel(
    private val reservationRepository: ReceiveReservations,
    private val userRepository: AccessUser
) : ViewModel() {
    private var _reservations: List<Reservation> = emptyList()
    private var _filteredReservations: MutableStateFlow<SearchResult<List<Reservation>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredReservations = _filteredReservations.asStateFlow()
    val user: StateFlow<FirebaseUser?> = userRepository.user

    init {
        viewModelScope.launch {
            user.collect {
                if(it != null) {
                    getReservations()
                }
            }
        }
    }
    fun getReservations() = viewModelScope.launch {
        when (val result = user.value?.let { reservationRepository.getReservationByUser(it.uid) }) {
            is FirebaseResult.Success -> {
                _reservations = result.data
                searchReservations("")
            }
            is FirebaseResult.Error -> _filteredReservations.update {
                SearchResult.Error(result.exception)
            }
        }
    }
    suspend fun searchReservations(query: String?) {
        _filteredReservations.update { startSearchReservations(query) }
    }
    private suspend fun startSearchReservations(query: String?): SearchResult<List<Reservation>> =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank())
                cont.resume(SearchResult.Success(_reservations))
            else {
                val resultList = mutableListOf<Reservation>()
                for (r in _reservations) {
                    if (r.establishment.name.lowercase().contains(query.lowercase())) resultList.add(r)
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))            }
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