package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.mco.group9.studyshare.databinding.RegisterPageBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterPageBinding
    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {

        val firstName = binding.inputFirstName.text.toString().trim()
        val lastName = binding.inputLastName.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString()
        val confirmPassword = binding.inputConfirmPassword.text.toString()

        if (firstName.isEmpty()) {
            binding.inputFirstName.error = "First name is required"
            return
        }

        if (lastName.isEmpty()) {
            binding.inputLastName.error = "Last name is required"
            return
        }

        if (email.isEmpty()) {
            binding.inputEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Invalid email format"
            return
        }

        if (password.length < 6) {
            binding.inputPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.inputConfirmPassword.error = "Passwords do not match"
            return
        }

        registerUser(firstName, lastName, email, password)
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        authRepo.registerUser(firstName, lastName, email, password) { success, error ->
            if (success) {
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                goToNextStep()
            } else {
                Toast.makeText(this, error ?: "Registration failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToNextStep() {

    }
}
