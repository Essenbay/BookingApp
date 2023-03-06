package com.example.bookingapp.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.bookingapp.R
import com.example.bookingapp.databinding.FragmentHomeBinding
import com.example.bookingapp.viewmodels.HomeViewModel
import com.example.bookingapp.viewmodels.SearchResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeHomeFragment : Fragment() {
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEstablishmentsView.setOnQueryTextListener(object : OnQueryTextListener {
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

    private fun handleFilteredEstablishments(result: SearchResult) {
        when (result) {
            is SearchResult.Success -> {
                var resultStr = "Establishments: \n"
                for (e in result.establishments) resultStr += e.name + '\n'
                binding.establishments.text = resultStr
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
            is SearchResult.Empty -> {
                binding.establishments.text = ""
                binding.emptyResultMsg.visibility = View.VISIBLE
            }
            is SearchResult.Error -> {
                binding.emptyResultMsg.visibility = View.INVISIBLE
                view?.let { Snackbar.make(it, "Something went wrong...", Snackbar.LENGTH_LONG) }
                Log.d("HomeFragment", result.exception.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}