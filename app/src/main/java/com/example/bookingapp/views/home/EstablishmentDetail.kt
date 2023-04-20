package com.example.bookingapp.views.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.databinding.FragmentHomeEstablishmentDetailBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalTime

class EstablishmentDetail : Fragment() {
    private var _binding: FragmentHomeEstablishmentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: EstablishmentDetailArgs by navArgs()
    private val viewModel: HomeEstablishmentDetailViewModel by navGraphViewModels(R.id.home_navigation) {
        HomeEstablishmentDetailViewModel.getFactory(
            args.establishmentID
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeEstablishmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        binding.establishmentContent.visibility = View.INVISIBLE

        binding.closeBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.establishment.collect {
                    when (it) {
                        is FirebaseResult.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE

                            binding.establishmentContent.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE

                            val est = it.data
                            binding.establishmentName.text = est.name
                            binding.establishmentDescription.text = est.description
                            binding.establishmentAddress.text = est.address
                            val workingTimeStartStr =
                                DateFormat.format("HH:mm", est.workingTimeStart.toDate())
                            val workingTimeEndStr =
                                DateFormat.format("HH:mm", est.workingTimeEnd.toDate())
                            binding.establishmentTime.text = getString(
                                R.string.establishment_time,
                                workingTimeStartStr,
                                workingTimeEndStr
                            )
                            binding.establishmentPhoneNumbers.text = est.phoneNumbers
                            binding.establishmentTableNumber.text = est.tableNumber.toString()

                            binding.createReservationBtn.setOnClickListener {
                                findNavController().navigate(
                                    EstablishmentDetailDirections.toCreateReservation(
                                        est
                                    )
                                )
//                                createReservation(est, est.tableNumber, Timestamp.now())
                            }
                        }
                        is FirebaseResult.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE

                            Snackbar.make(view, "${it.exception}", Snackbar.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }
                        else -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}