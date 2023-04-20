package com.example.bookingapp.views.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.example.bookingapp.R
import com.example.bookingapp.databinding.FragmentEditUserInfoBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.viewmodels.AccountViewModel
import kotlinx.coroutines.launch

class EditUserInfoFragment : Fragment() {
    private var _binding: FragmentEditUserInfoBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AccountViewModel by navGraphViewModels(R.id.auth_navigation) { AccountViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editFullName.setText(viewModel.user.value?.displayName)
        binding.saveChanges.setOnClickListener {
            val newFullName = binding.editFullName.text.toString()
            if (newFullName.isEmpty() || newFullName.isEmpty()) Toast.makeText(
                context,
                "Fields mustn't be empty!",
                Toast.LENGTH_LONG)
                .show()
            else editUserInfo(newFullName, view)
        }
    }

    private fun editUserInfo(fullName: String, view: View) =
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            when (val result = viewModel.editUserInfo(fullName)) {
                is FirebaseResult.Success -> {
                    Toast.makeText(context, "Successfully changed user info", Toast.LENGTH_LONG)
                        .show()
                    val action = AccountSignUpFragmentDirections.toAccount()
                    view.findNavController().navigate(action)
                }
                is FirebaseResult.Error -> {
                    Toast.makeText(
                        context,
                        result.exception.localizedMessage ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            binding.progressBar.visibility = View.INVISIBLE
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}