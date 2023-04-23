package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.ReviewUI
import com.google.firebase.Timestamp

interface ReviewRepository {
//    suspend fun getReviews(): List<Review?>
//    suspend fun getReviewById(id: String): ReviewUI
    suspend fun getReviewsByEstablishment(establishmentId: String): List<ReviewUI>
    suspend fun createReview(
        userId: String,
        establishmentId: String,
        dateOfCreation: Timestamp,
        rate: Float,
        comment: String
    ): Boolean
}