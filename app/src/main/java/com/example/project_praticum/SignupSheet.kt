package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class SignupSheet : BottomSheetDialogFragment() {

    private lateinit var sessionManager: SessionManager

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnShowPassword: ImageView
    private lateinit var btnShowConfirmPassword: ImageView
    private lateinit var progressBar: ProgressBar

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        btnShowPassword = view.findViewById(R.id.btnShowPassword)
        btnShowConfirmPassword = view.findViewById(R.id.btnShowConfirmPassword)
        progressBar = view.findViewById(R.id.progressBar)

        btnSignUp.setOnClickListener {
            signup()
        }

        btnShowPassword.setOnClickListener {
            togglePassword()
        }

        btnShowConfirmPassword.setOnClickListener {
            toggleConfirmPassword()
        }
    }

    private fun signup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Username is required"
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Password does not match"
            return
        }

        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.register(
                    SignupRequest(
                        name = name,
                        email = email,
                        password = password,
                        password_confirmation = confirmPassword
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!

                    sessionManager.saveAuth(auth.token, auth.user)

                    Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)

                    activity?.finish()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Signup failed. Email may already exist.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun togglePassword() {
        isPasswordVisible = !isPasswordVisible

        etPassword.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        etPassword.setSelection(etPassword.text.length)
    }

    private fun toggleConfirmPassword() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible

        etConfirmPassword.inputType = if (isConfirmPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        etConfirmPassword.setSelection(etConfirmPassword.text.length)
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSignUp.isEnabled = !isLoading
    }
}