package com.mobdeve.s18.mco.group9.studyshare


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regNextBtn.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {

        val firstName = binding.regFirstNameEt.text.toString().trim()
        val lastName = binding.regLastNameEt.text.toString().trim()
        val email = binding.regEmailEt.text.toString().trim()
        val password = binding.regPasswordEt.text.toString()
        val confirmPassword = binding.regConfirmPassEt.text.toString()

        if (firstName.isEmpty()) {
            binding.regFirstNameEt.error = "First name is required"
            return
        }

        if (lastName.isEmpty()) {
            binding.regLastNameEt.error = "Last name is required"
            return
        }

        if (email.isEmpty()) {
            binding.regEmailEt.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.regEmailEt.error = "Invalid email format"
            return
        }

        if (password.length < 6) {
            binding.regPasswordEt.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.regConfirmPassEt.error = "Passwords do not match"
            return
        }

        registerUser(firstName, lastName, email, password)
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        authRepo.registerUser(firstName, lastName, email, password) { success, error, uid ->
            if (success) {
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                goToNextStep(uid!!)
            } else {
                Toast.makeText(this, error ?: "Registration failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToNextStep(uid: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ID", uid)
        startActivity(intent)
        finish()
    }
}
