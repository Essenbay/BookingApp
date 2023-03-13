package com.example.bookingapp.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingapp.databinding.FragmentHomeBinding
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.example.bookingapp.R
import com.example.bookingapp.adapters.EstablishmentsAdapter
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.Timestamp


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.establishments.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearch(null)

        binding.homeToolbar.inflateMenu(R.menu.home_menu)
        binding.homeToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    true
                }
                R.id.add_establishment -> {
                    val action = HomeFragmentDirections.toAddEstablishment()
                    findNavController().navigate(action)
                    true
                }
                R.id.update_establishment_list -> {
                    viewModel.getEstablishments()
                    true
                }
                else -> false
            }
        }
        val searchItem = binding.homeToolbar.menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : OnQueryTextListener {
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
                viewModel.filteredEstablishments.collect {
                    handleFilteredEstablishments(it)
                }
            }
        }
    }

    private fun onSearch(query: String?) = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.searchEstablishments(query)
    }

    //Todo: Progress bar is not showing
    private fun handleFilteredEstablishments(result: SearchResult<List<Establishment>>) {
        when (result) {
            is SearchResult.Success -> {
                binding.establishments.visibility = View.VISIBLE
                binding.establishments.adapter =
                    EstablishmentsAdapter(result.result) { establishment, tableID, date ->
                        createReservation(establishment, tableID, date)
                    }
                Log.d("HomeFragment", "Store: ${result.result.toString()}")

                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
            is SearchResult.Empty -> {
                binding.establishments.visibility = View.INVISIBLE
                binding.emptyResultMsg.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }
            is SearchResult.Error -> {
                binding.emptyResultMsg.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                view?.let { Snackbar.make(it, "Something went wrong...", Snackbar.LENGTH_LONG) }
            }
            is SearchResult.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.establishments.visibility = View.INVISIBLE
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
        }
    }

    private fun createReservation(
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ) = viewLifecycleOwner.lifecycleScope.launch {
        when (val result = viewModel.createReservation(establishment, tableID, date)) {
            is FirebaseResult.Success -> {
                Toast.makeText(context, "The reservation was created", Toast.LENGTH_LONG).show()
            }
            is FirebaseResult.Error -> {
                Toast.makeText(context, result.exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}