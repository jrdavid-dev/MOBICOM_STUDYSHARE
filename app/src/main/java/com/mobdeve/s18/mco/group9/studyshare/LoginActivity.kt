package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.mco.group9.studyshare.databinding.LoginPageBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginPageBinding
    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.goToRegister.setOnClickListener {
            // TODO: Start RegisterActivity if needed
        }
    }

    private fun handleLogin() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString()

        if (email.isEmpty()) {
            binding.inputEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Invalid email format"
            return
        }

        if (password.isEmpty()) {
            binding.inputPassword.error = "Password is required"
            return
        }

        loginUser(email, password)
    }

    private fun loginUser(email: String, password: String) {
        authRepo.loginUser(email, password) { success, user, error ->
            if (success) {
                Toast.makeText(this, "Welcome back ${user?.firstName}", Toast.LENGTH_SHORT).show()
                goToNextStep()
            } else {
                Toast.makeText(this, error ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToNextStep() {

    }
}
