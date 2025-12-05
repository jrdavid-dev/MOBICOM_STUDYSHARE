package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ProfilePageBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ProfileActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    private lateinit var binding: ProfilePageBinding
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (current_user_id == null) {
            redirectToLogin()
            return
        }

        setupLogout()
        loadUserProfile()
        loadUserCourses()

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupLogout() {
        binding.logoutBtn.setOnClickListener {
            auth.signOut()


            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadUserProfile() {
        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(current_user_id!!)
            .get()
            .addOnSuccessListener { doc ->

                val firstName = doc.getString(MyFirestoreReferences.FIRSTNAME_FIELD) ?: ""
                val lastName = doc.getString(MyFirestoreReferences.LASTNAME_FIELD) ?: ""

                val initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()

                binding.profileInitialsTv.text = initials
                binding.profileNameTv.text = "$firstName $lastName"


                binding.profileDescriptionTv.text = ""
                binding.profileBioTv.text = ""
            }
    }

    private fun loadUserCourses() {
        val userId = current_user_id ?: return
        val db = Firebase.firestore

        val coursesQuery = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_AUTHOR_FIELD, userId)

        val options = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(coursesQuery, Course::class.java)
            .build()

        profileAdapter = ProfileAdapter(options)

        binding.profileCoursesRecyclerView.apply {
            itemAnimator = null
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(this@ProfileActivity)
        }

        profileAdapter.startListening()

    }



    override fun onStart() {
        super.onStart()
        if (::profileAdapter.isInitialized) {
            profileAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::profileAdapter.isInitialized) {
            profileAdapter.stopListening()
        }
    }
}
