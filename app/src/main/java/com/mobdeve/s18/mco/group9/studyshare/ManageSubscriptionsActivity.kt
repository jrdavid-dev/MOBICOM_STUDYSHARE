package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ManageSubscriptionsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ManageSubscriptionsActivity : AppCompatActivity() {


    private lateinit var manageSubscriptionsAdapter: ManageSubscriptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: ManageSubscriptionsBinding = ManageSubscriptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        val db = Firebase.firestore


        val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)

        val coursesQuery = coursesRef.orderBy("createdAt", Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(coursesQuery, Course::class.java)
            .build()

        manageSubscriptionsAdapter = ManageSubscriptionsAdapter(options)

        viewBinding.manageSubscriptionsRecyclerView.adapter = manageSubscriptionsAdapter
        viewBinding.manageSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        this.manageSubscriptionsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        this.manageSubscriptionsAdapter.stopListening()
    }
}

