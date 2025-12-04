package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // IMPORTANT: must match activity_login.xml
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            handleLogin()
        }

        binding.registerLinkTv.setOnClickListener {
            // TODO: Start RegisterActivity
        }
    }

    private fun handleLogin() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString()

        if (email.isEmpty()) {
            binding.emailEt.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid email format"
            return
        }

        if (password.isEmpty()) {
            binding.passwordEt.error = "Password is required"
            return
        }

        loginUser(email, password)
    }

    private fun loginUser(email: String, password: String) {
        authRepo.loginUser(email, password) { success, user, error ->
            if (success) {
                Toast.makeText(
                    this,
                    "Welcome back ${user?.firstName ?: ""}",
                    Toast.LENGTH_SHORT
                ).show()
                goToNextStep()
            } else {
                Toast.makeText(this, error ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToNextStep() {

    }
}
