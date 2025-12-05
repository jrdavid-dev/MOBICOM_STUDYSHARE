package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityEditProfileBinding

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get passed data
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val username = intent.getStringExtra("username") ?: ""

        // Populate fields
        binding.nameEt.setText(firstName)
        binding.surnameEt.setText(lastName)
        binding.usernameEt.setText(username)

        binding.profileBtn.setOnClickListener {
            finish()
        }

        setupEditButtons()
    }

    private fun setupEditButtons() {

        binding.updateBtn.setOnClickListener { saveChanges() }

    }

    private fun saveChanges() {

        val uid = auth.currentUser?.uid ?: return

        val updatedFirstName = binding.nameEt.text.toString().trim()
        val updatedLastName = binding.surnameEt.text.toString().trim()
        val updatedUsername = binding.usernameEt.text.toString().trim()

        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedUsername.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            MyFirestoreReferences.FIRSTNAME_FIELD to updatedFirstName,
            MyFirestoreReferences.LASTNAME_FIELD to updatedLastName,
            MyFirestoreReferences.USERNAME_FIELD to updatedUsername
        )

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
