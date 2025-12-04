package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ManageSubscriptionsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Course
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.Query
import com.mobdeve.s18.mco.group9.studyshare.models.Material


class ManageSubscriptionsActivity : AppCompatActivity() {
    private lateinit var manageSubscriptionAdapter: ManageSubscriptionsAdapter
    private lateinit var viewBinding: ManageSubscriptionsBinding
    private val remainingCourses = ArrayList<Course>()
    private val current_user_id = "1001"
    private var currentSearchText = ""
    private var currentTab = 0 // Track which tab is active

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ManageSubscriptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setupTabLayout()
        setupSearch()
        loadSubscribedCourses()
    }

    private fun loadSubscribedCourses() {
        currentTab = 0 // Set current tab
        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscriptionCourseIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.COURSE_ID_FIELD)
                }

                if (subscriptionCourseIds.isEmpty()) {
                    Log.d("MANAGE_SUBS", "User has no subscriptions")
                    viewBinding.manageSubscriptionsRecyclerView.adapter = null
                    return@addOnSuccessListener
                }

                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                var coursesQuery: Query = coursesRef.whereIn(
                    MyFirestoreReferences.ID_FIELD,
                    subscriptionCourseIds
                )

                // Apply search filter if exists
                if (currentSearchText.isNotEmpty()) {
                    coursesQuery = coursesQuery
                        .orderBy(MyFirestoreReferences.COURSE_NAME_FIELD)
                        .startAt(currentSearchText)
                        .endAt(currentSearchText + "\uf8ff")
                } else {
                    coursesQuery = coursesQuery.orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)
                }

                val options = FirestoreRecyclerOptions.Builder<Course>()
                    .setQuery(coursesQuery, Course::class.java)
                    .build()

                if (::manageSubscriptionAdapter.isInitialized) {
                    manageSubscriptionAdapter.stopListening()
                }

                manageSubscriptionAdapter = ManageSubscriptionsAdapter(options, true)

                viewBinding.manageSubscriptionsRecyclerView.itemAnimator = null
                viewBinding.manageSubscriptionsRecyclerView.adapter = manageSubscriptionAdapter
                viewBinding.manageSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(this)

                manageSubscriptionAdapter.startListening()

                Log.d("MANAGE_SUBS", "Loaded ${subscriptionCourseIds.size} subscribed courses")
            }
            .addOnFailureListener { exception ->
                Log.w("MANAGE_SUBS", "Error getting subscribed courses", exception)
            }
    }

    private fun loadAvailableCourses() {
        currentTab = 1 // Set current tab
        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscriptionCourseIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.COURSE_ID_FIELD)
                }

                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                var coursesQuery: Query = if (subscriptionCourseIds.isEmpty()) {
                    coursesRef
                } else {

                    coursesRef.whereNotIn(
                        MyFirestoreReferences.ID_FIELD,
                        subscriptionCourseIds
                    )
                }

                // Apply search filter if exists
                if (currentSearchText.isNotEmpty()) {
                    coursesQuery = coursesQuery
                        .orderBy(MyFirestoreReferences.COURSE_NAME_FIELD)
                        .startAt(currentSearchText)
                        .endAt(currentSearchText + "\uf8ff")
                } else {
                    coursesQuery = coursesQuery.orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)
                }

                val options = FirestoreRecyclerOptions.Builder<Course>()
                    .setQuery(coursesQuery, Course::class.java)
                    .build()

                if (::manageSubscriptionAdapter.isInitialized) {
                    manageSubscriptionAdapter.stopListening()
                }

                manageSubscriptionAdapter = ManageSubscriptionsAdapter(options, false)

                viewBinding.manageSubscriptionsRecyclerView.itemAnimator = null
                viewBinding.manageSubscriptionsRecyclerView.adapter = manageSubscriptionAdapter
                viewBinding.manageSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(this)

                manageSubscriptionAdapter.startListening()

                Log.d("MANAGE_SUBS", "Loaded available courses")
            }
            .addOnFailureListener { exception ->
                Log.w("MANAGE_SUBS", "Error getting available courses", exception)
            }
    }

    private fun setupTabLayout() {
        viewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        loadSubscribedCourses()
                    }
                    1 -> {
                        loadAvailableCourses()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        viewBinding.searchButton.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString().trim()
                updateQuery()
            }
        })
    }

    private fun updateQuery() {
        when (currentTab) {
            0 -> loadSubscribedCourses()
            1 -> loadAvailableCourses()
        }
    }

    override fun onStart() {
        super.onStart()
        if (::manageSubscriptionAdapter.isInitialized) {
            manageSubscriptionAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::manageSubscriptionAdapter.isInitialized) {
            manageSubscriptionAdapter.stopListening()
        }
    }
}