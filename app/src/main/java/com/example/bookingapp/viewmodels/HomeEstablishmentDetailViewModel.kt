package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.ReviewUI
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.data.repositories.ReviewRepository
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeEstablishmentDetailViewModel @Inject constructor(
    private val establishmentRepository: EstablishmentRepository,
    private val accessUser: AccessUser,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val _establishment: MutableStateFlow<FirebaseResult<Establishment>?> =
        MutableStateFlow(null)
    val establishment: StateFlow<FirebaseResult<Establishment>?> =
        _establishment.asStateFlow()
    private val _reviews: MutableStateFlow<List<ReviewUI>> = MutableStateFlow(mutableListOf())
    val reviews: StateFlow<List<ReviewUI>> = _reviews.asStateFlow()
    lateinit var establishmentID: String

    init {
        viewModelScope.launch {
            _establishment.update {
                establishmentRepository.getEstablishment(establishmentID).also { estResult ->
                    when (estResult) {
                        is FirebaseResult.Success -> _reviews.update {
                            reviewRepository.getReviewsByEstablishment(
                                estResult.data.establishmentId
                            )

                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    suspend fun createReservation(
        establishmentId: String,
        tableID: Int,
        fromDate: Timestamp,
        toDate: Timestamp
    ): FirebaseResult<Boolean> {
        val userUID = accessUser.user.value?.uid
        return if (userUID == null) FirebaseResult.Error(UserNotSignedIn())
        else {
            establishmentRepository.createReservation(
                userUID,
                establishmentId,
                tableID,
                fromDate, toDate
            )
        }
    }

    suspend fun createReview(
        establishmentId: String,
        dateOfCreation: Timestamp,
        rate: Float,
        comment: String
    ): Boolean {
        val userId = accessUser.user.value?.uid
        return if (userId == null) false
        else reviewRepository.createReview(userId, establishmentId, dateOfCreation, rate, comment)
    }
}

