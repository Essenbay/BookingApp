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
import com.example.bookingapp.databinding.FragmentAccountSignUpBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.util.checkPasswordMatching
import com.example.bookingapp.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSignUpFragment : Fragment() {
    private var _binding: FragmentAccountSignUpBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
//            editSignupEmail.doOnTextChanged { text, _, _, _ ->
//                viewModel.userInputState.update {
//                    it.copy(emailInput = text.toString())
//                }
//            }
//            editSingupPassword.doOnTextChanged { text, _, _, _ ->
//                viewModel.userInputState.update {
//                    it.copy(passwordInput = text.toString())
//                }
//            }
//            editSigUpConfirmPassword.doOnTextChanged { text, _, _, _ ->
//                viewModel.userInputState.update {
//                    it.copy(confirmPasswordInput = text.toString())
//                }
//            }
            //Todo: Add phone number formatter
            signUpBtn.setOnClickListener {
                val fullName = editSignupFullName.text.toString().trim()
                val email = editSignupEmail.text.toString().trim()
                val password = editSingupPassword.text.toString().trim()
                val confirmPassword = editSigUpConfirmPassword.text.toString().trim()

                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || fullName.isBlank()) {
                    Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
                } else if (!checkLoginField(email)) {
                    Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
                } else if (!checkPasswordMatching(password, confirmPassword)) {
                    Toast.makeText(context, "Passwords are not matching", Toast.LENGTH_LONG).show()
                } else if (!checkPasswordField(password)) {
                    Toast.makeText(
                        context,
                        "Password must have at least 6 characters",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    binding.progressCircular.visibility = View.VISIBLE
                    register(fullName, email, password, view)
                }
            }
            toLogInLink.setOnClickListener {
//                viewModel.userInputState.update {
//                    it.copy(passwordInput = "", confirmPasswordInput = "")
//                }
                view.findNavController().navigateUp()
            }
        }
    }

    private fun register(fullName: String, email: String, password: String, view: View) =
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressCircular.visibility = View.VISIBLE
            when (val result = viewModel.register(fullName, email, password)) {
                is FirebaseResult.Success -> {
                    Toast.makeText(context, "Successfully registered", Toast.LENGTH_LONG)
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
                    binding.progressCircular.visibility = View.INVISIBLE
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}