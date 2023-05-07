package com.example.bookingapp.views.reservationhistory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookingapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations)
    }
}