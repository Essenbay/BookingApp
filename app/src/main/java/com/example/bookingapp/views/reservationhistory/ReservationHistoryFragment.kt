package com.example.bookingapp.views.reservationhistory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.databinding.FragmentReservationHistoryBinding
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.viewmodels.ReservationsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReservationHistoryFragment : Fragment() {
    private var _binding: FragmentReservationHistoryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: ReservationsViewModel by viewModels { ReservationsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReservationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reservationsToolbar.inflateMenu(R.menu.reservations_menu)
        binding.reservationsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.update_reservation_list -> {
                    viewModel.getReservations()
                    true
                }
                else -> false
            }
        }
        val searchItem = binding.reservationsToolbar.menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onSearch(newText)
                return false
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredReservations.collect {
                    handleFilteredReservations(it)
                }
            }
        }
    }

    private fun onSearch(query: String?) = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.searchReservations(query)
    }

    //Todo: when first entering app loading state is staying until move from other fragments
    private fun handleFilteredReservations(result: SearchResult<List<Reservation>>) {
        when (result) {
            is SearchResult.Success -> {
                var resultStr = "Reservations: \n"
                for (e in result.result) resultStr += e.establishmentId + '\n'
                binding.reservations.text = resultStr
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
            is SearchResult.Empty -> {
                binding.reservations.text = ""
                binding.emptyResultMsg.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }
            is SearchResult.Error -> {
                binding.emptyResultMsg.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                view?.let { Snackbar.make(it, "Something went wrong...", Snackbar.LENGTH_LONG) }
                Log.d("HomeFragment", result.exception.toString())
            }
            is SearchResult.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.reservations.visibility = View.INVISIBLE
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}