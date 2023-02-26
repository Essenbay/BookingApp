package com.example.bookingapp.views.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.bookingapp.data.firebaseauth.AuthState
import com.example.bookingapp.databinding.FragmentAccountSignUpBinding
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.util.checkPasswordMatching
import com.example.bookingapp.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountSignUpFragment : Fragment() {
    private var _binding: FragmentAccountSignUpBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            editSignupEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(emailInput = text.toString())
                }
            }
            editSingupPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(passwordInput = text.toString())
                }
            }
            editSigUpConfirmPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(confirmPasswordInput = text.toString())
                }
            }
            signUpBtn.setOnClickListener {
                val email = editSignupEmail.text.toString()
                val password = editSingupPassword.text.toString()
                val confirmPassword = editSigUpConfirmPassword.text.toString()

                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
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
                    viewModel.register(email, password)
                }
            }
            toLogInLink.setOnClickListener {
                viewModel.userInputState.update {
                    it.copy(passwordInput = "", confirmPasswordInput = "")
                }
                view.findNavController().navigateUp()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userData.collect { state ->
                when (state.resultState) {
                    //Todo: Add progress dialog
                    AuthState.Companion.State.LOADING -> {
                        Log.d("AccountSignUp", "Loading")
                    }
                    AuthState.Companion.State.FAILURE -> {
                        Log.d("AccountSignUp", "Failure")
                        Toast.makeText(context, "Wrong email or password", Toast.LENGTH_LONG).show()
                    }
                    AuthState.Companion.State.SUCCESSFUL -> {
                        Log.d("AccountSignUp", "Successful")
                        val action = AccountSignUpFragmentDirections.toAccount()
                        viewModel.userInputState.update {
                            it.copy(passwordInput = "", emailInput = "")
                        }
                        view.findNavController().navigate(action)
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}