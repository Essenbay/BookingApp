package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingapp.data.models.ReservationWithEstablishment
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val reservationRepository: ReceiveReservations,
    private val userRepository: AccessUser
) : ViewModel() {
    private var _reservationEstablishments: List<ReservationWithEstablishment> = listOf()
    private var _filteredReservations: MutableStateFlow<SearchResult<List<ReservationWithEstablishment>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredReservations = _filteredReservations.asStateFlow()
    val user: StateFlow<FirebaseUser?> = userRepository.user
    private var _progressBarVisibility: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val progressBarVisibility: StateFlow<Boolean> = _progressBarVisibility.asStateFlow()

    init {
        getReservations()
    }
    fun getReservations() = viewModelScope.launch {
        _progressBarVisibility.update { true }
        user.collect {
            if (it != null) {
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
            _progressBarVisibility.update { false }
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
}
