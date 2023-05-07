package com.example.bookingapp.views.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookingapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
    }
}