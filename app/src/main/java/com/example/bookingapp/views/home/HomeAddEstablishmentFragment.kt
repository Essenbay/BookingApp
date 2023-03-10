package com.example.bookingapp.views.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.databinding.FragmentHomeAddEstablishmentBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkPhoneNumberField
import com.example.bookingapp.viewmodels.HomeViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalTime
import java.time.format.DateTimeParseException

class HomeAddEstablishmentFragment : Fragment() {
    private var _binding: FragmentHomeAddEstablishmentBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }

    private lateinit var workingTimeStart: Timestamp
    private lateinit var workingTimeEnd: Timestamp
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeAddEstablishmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Todo: Delete following:
        binding.editTitle.setText("Cafe")
        binding.editDescription.setText("The place for you")
        binding.editAddress.setText("Almaty")

        binding.addEstablishmentToolbar.inflateMenu(R.menu.add_establishment_menu)
        binding.addEstablishmentToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.send_establishment -> {
                    createEstablishment()
                    true
                }
                else -> false
            }
        }

        binding.editWorkingTimeStart.setOnClickListener {
            it as EditText
            val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = LocalTime.of(hourOfDay, minute)
                workingTimeStart = Timestamp(time.toSecondOfDay().toLong(), 0)
                it.setText(time.toString())
            }
            val initialTime = LocalTime.MIDNIGHT
            TimePickerDialog(
                context, listener, initialTime.hour, initialTime.minute, true
            ).show()
        }

        binding.editWorkingTimeEnd.setOnClickListener {
            it as EditText
            val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = LocalTime.of(hourOfDay, minute)
                workingTimeEnd = Timestamp(time.toSecondOfDay().toLong(), 0)
                it.setText(time.toString())            }
            val initialTime = LocalTime.MIDNIGHT
            TimePickerDialog(
                context, listener, initialTime.hour, initialTime.minute, true
            ).show()
        }
    }

    private fun createEstablishment() {
        try {
            val title = binding.editTitle.text.toString()
            val description = binding.editDescription.text.toString()
            val address = binding.editAddress.text.toString()
            val phoneNumber = binding.editPhoneNumber.text.toString()

            if (title.isBlank() || description.isBlank() || address.isBlank() || phoneNumber.isBlank() || !this::workingTimeStart.isInitialized || !this::workingTimeEnd.isInitialized)
                Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
            else if (!checkPhoneNumberField(phoneNumber))
                Toast.makeText(context, "Phone number is badly formatted", Toast.LENGTH_LONG).show()
            else {
                val newEstablishment = Establishment(
                    title,
                    description,
                    address,
                    workingTimeStart,
                    workingTimeEnd,
                    phoneNumber
                )
                startCreatingEstablishment(newEstablishment)
            }
        } catch (e: DateTimeParseException) {
            Toast.makeText(context, "Date is badly formatted", Toast.LENGTH_LONG).show()
        }


    }

    private fun startCreatingEstablishment(establishment: Establishment) =
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = viewModel.addEstablishment(establishment)) {
                is FirebaseResult.Success -> {
                    Toast.makeText(context, "Request send", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp()
                }
                is FirebaseResult.Error -> {
                    Toast.makeText(context, result.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}