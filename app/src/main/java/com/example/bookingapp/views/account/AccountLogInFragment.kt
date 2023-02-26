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
import com.example.bookingapp.databinding.FragmentAccountLogInBinding
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountLogInFragment : Fragment() {
    private var _binding: FragmentAccountLogInBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            editLoginEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(emailInput = text.toString())
                }
            }
            editLoginPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(passwordInput = text.toString())
                }
            }

            loginBtn.setOnClickListener {
                val email = editLoginEmail.text.toString()
                val password = editLoginPassword.text.toString()
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
                } else if(!checkLoginField(email)){
                    Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
                } else if(!checkPasswordField(password)){
                    Toast.makeText(context, "Password must have at least 6 characters", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.login(email, password)
                }
            }
            toSignupBtn.setOnClickListener {
                Log.d("Login fragment", "on register")
                val action = AccountLogInFragmentDirections.logInToSignUp()
                viewModel.userInputState.update {
                    it.copy(passwordInput = "", emailInput = "")
                }
                view.findNavController().navigate(action)
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
                        viewModel.userInputState.update {
                            it.copy(passwordInput = "", emailInput = "")
                        }
                        view.findNavController().popBackStack()
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