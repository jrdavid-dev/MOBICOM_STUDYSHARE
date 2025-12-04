package com.mobdeve.s18.mco.group9.studyshare


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ProfilePageBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ProfileActivity : AppCompatActivity() {


    private val current_user_id = "1001"
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: ProfilePageBinding = ProfilePageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.firestore

        val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)

        val coursesQuery =
            coursesRef.whereEqualTo(MyFirestoreReferences.COURSE_AUTHOR_FIELD, current_user_id)

        val options = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(coursesQuery, Course::class.java)
            .build()

        profileAdapter = ProfileAdapter(options)

        viewBinding.profileCoursesRecyclerView.itemAnimator = null


        viewBinding.profileCoursesRecyclerView.adapter = profileAdapter
        viewBinding.profileCoursesRecyclerView.layoutManager = LinearLayoutManager(this)


    }

    override fun onStart() {
        super.onStart()
        this.profileAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        this.profileAdapter.stopListening()
    }
}

