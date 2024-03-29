package com.example.bookingapp.views.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.bookingapp.databinding.FragmentEditAccountBinding
import com.example.bookingapp.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountFragment : Fragment() {
    private var _binding: FragmentEditAccountBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toEditProfileBtn.setOnClickListener {
            view.findNavController()
                .navigate(EditAccountFragmentDirections.toEditUserInfo())
        }
        binding.toEditEmailBtn.setOnClickListener {
            view.findNavController().navigate(EditAccountFragmentDirections.toEditEmail())
        }
        binding.toEditPasswordBtn.setOnClickListener {
            view.findNavController().navigate(EditAccountFragmentDirections.toEditPassword())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}