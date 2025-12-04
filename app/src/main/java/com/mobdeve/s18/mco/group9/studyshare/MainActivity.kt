package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityMainBinding
import com.mobdeve.s18.mco.group9.studyshare.databinding.ManageSubscriptionsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class MainActivity : AppCompatActivity() {


    private val TAG = "MAIN_ACTIVITY"
    private lateinit var materialAdapter : MaterialAdapter
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var viewBinding: ActivityMainBinding
    private val current_user_id = "1001"

    private val db by lazy { Firebase.firestore }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        loadRecentUploads()
        loadSubscribedCourses()
        setupClickListeners()
        setupBottomNavigation()




    }
    private fun setupBottomNavigation() {
        viewBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_search -> {
                    navigateToSearch()
                    true
                }
                R.id.nav_notifs -> {
                    navigateToNotifications()
                    true
                }
                R.id.nav_profile -> {
                    navigateToProfile()
                    true
                }
                else -> false
            }
        }
    }
    private fun setupClickListeners() {
        viewBinding.apply {

            searchButton.setOnClickListener { navigateToSearch() }
            searchIv.setOnClickListener { navigateToSearch() }
            seeAllUploadsTv.setOnClickListener { navigateToRecentUploads() }
            fabUploadBtn.setOnClickListener { navigateToUpload() }
            manageSubsTv.setOnClickListener { navigateToManageSubscriptions() }
        }
    }
    // TODO add notif
    private fun navigateToProfile(){
        startActivity(Intent(this, ProfileActivity::class.java))
    }
    private fun navigateToNotifications(){
        startActivity(Intent(this, NotificationsActivity::class.java))
    }
    private fun navigateToSearch() {
        startActivity(Intent(this, SearchPageActivity::class.java))
    }

    private fun navigateToRecentUploads() {
        startActivity(Intent(this, RecentUploadsActivity::class.java))
    }

    private fun navigateToUpload() {
        startActivity(Intent(this, UploadMaterialActivity::class.java))
    }

    private fun navigateToManageSubscriptions() {
        startActivity(Intent(this, ManageSubscriptionsActivity::class.java))
    }

    private fun loadRecentUploads(){

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val materialsQuery = materialsRef.orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING).limit(10)


        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(materialsQuery, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, current_user_id, false)
        viewBinding.uploadsRecyclerView.itemAnimator = null


        viewBinding.uploadsRecyclerView.adapter = materialAdapter
        viewBinding.uploadsRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }
    private fun loadSubscribedCourses() {

        if (::courseAdapter.isInitialized) {
            courseAdapter.stopListening()
        }

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscriptionCourseIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.COURSE_ID_FIELD)
                }

                if (subscriptionCourseIds.isEmpty()) {
                    Log.d(TAG, "User has no subscriptions")
                    viewBinding.subsRecyclerView.adapter = null
                    viewBinding.subsRecyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                viewBinding.subsRecyclerView.visibility = View.VISIBLE

                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                val coursesQuery: Query = coursesRef.whereIn(
                    MyFirestoreReferences.ID_FIELD,
                    subscriptionCourseIds
                )

                val options = FirestoreRecyclerOptions.Builder<Course>()
                    .setQuery(coursesQuery, Course::class.java)
                    .build()

                courseAdapter = CourseAdapter(options)

                viewBinding.subsRecyclerView.itemAnimator = null
                viewBinding.subsRecyclerView.adapter = courseAdapter
                viewBinding.subsRecyclerView.layoutManager = GridLayoutManager(this, 2)

                courseAdapter.startListening()

                Log.d(TAG, "Loaded ${subscriptionCourseIds.size} subscribed courses")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting subscribed courses", exception)
            }
    }

    override fun onResume() {
        super.onResume()
        loadSubscribedCourses()
        viewBinding.bottomNavigation.selectedItemId = R.id.nav_home
    }
    override fun onStart() {
        super.onStart()
        this.materialAdapter.startListening()

        if (::courseAdapter.isInitialized) {
            courseAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        this.materialAdapter.stopListening()
        if (::courseAdapter.isInitialized) {
            courseAdapter.stopListening()
        }
    }



}

