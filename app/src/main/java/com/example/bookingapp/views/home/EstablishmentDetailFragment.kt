package com.example.bookingapp.views.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingapp.R
import com.example.bookingapp.adapters.ReviewsAdapter
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.ReviewUI
import com.example.bookingapp.databinding.FragmentHomeEstablishmentDetailBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EstablishmentDetailFragment : Fragment() {
    private var _binding: FragmentHomeEstablishmentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: EstablishmentDetailFragmentArgs by navArgs()
    private val viewModel: HomeEstablishmentDetailViewModel by lazy {
        val viewModel: HomeEstablishmentDetailViewModel by viewModels()
        viewModel.establishmentID = args.establishmentID
        viewModel
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.comments.layoutManager = LinearLayoutManager(requireContext())

        binding.btnCreateComment.setOnClickListener {
            createReview(view)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reviews.collect {
                    updateCommentUI(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.establishment.collect {
                    when (it) {
                        is FirebaseResult.Success -> {
                            updateEstablishmentInfoUi(it.data)
                        }
                        is FirebaseResult.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE

                            Snackbar.make(view, "${it.exception}", Snackbar.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }
                        else -> {
                            binding.createReservationBtn.visibility = View.INVISIBLE
                            binding.commentContent.visibility = View.INVISIBLE
                            binding.establishmentContent.visibility = View.INVISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun updateCommentUI(reviews: List<ReviewUI>) {
        binding.comments.adapter = ReviewsAdapter(reviews)
    }

    private fun updateEstablishmentInfoUi(est: Establishment) {
        binding.progressBar.visibility = View.INVISIBLE

        binding.createReservationBtn.visibility = View.VISIBLE
        binding.commentContent.visibility = View.VISIBLE
        binding.establishmentContent.visibility = View.VISIBLE


        //Show establishment info
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

        //Create reservation
        binding.createReservationBtn.setOnClickListener {
            findNavController().navigate(
                EstablishmentDetailFragmentDirections.toCreateReservation(
                    est
                )
            )
        }
    }

    private fun createReview(view: View) = try {
        viewLifecycleOwner.lifecycleScope.launch {
            val date = Timestamp.now()
            val rate = binding.ratingBar.rating
            val comment: String = binding.commentEdit.text.toString()
            if (comment.isEmpty()) Snackbar.make(view, "Print a comment", Snackbar.LENGTH_SHORT)
                .show()
            else {
                val result = viewModel.createReview(args.establishmentID, date, rate, comment)
                if (result) Snackbar.make(view, "Review successfully posted", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    } catch (e: Exception) {
        Snackbar.make(view, e.message ?: "Something went wrong...", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}