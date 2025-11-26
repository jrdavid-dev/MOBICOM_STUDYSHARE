package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.MainActivity.Companion.subscribedCourses
import com.mobdeve.s18.mco.group9.studyshare.databinding.ManageSubscriptionsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ManageSubscriptionsActivity : AppCompatActivity() {
    private lateinit var manageSubscriptionsAdapter: ManageSubscriptionsAdapter
    private val remainingCourses = ArrayList<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: ManageSubscriptionsBinding = ManageSubscriptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        manageSubscriptionsAdapter = ManageSubscriptionsAdapter(subscribedCourses, remainingCourses)

        viewBinding.manageSubscriptionsRecyclerView.adapter = manageSubscriptionsAdapter
        viewBinding.manageSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadRemainingCourses()

    }

    private fun loadRemainingCourses(){
        val db = Firebase.firestore
        val subscribeCourseIds = subscribedCourses.map { it.id }

        db.collection(MyFirestoreReferences.COURSES_COLLECTION)
            .orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { courses ->
                remainingCourses.clear()

                for(remCourse in courses.documents){
                    val course = remCourse.toObject(Course::class.java)
                    if (course != null && course.id !in subscribeCourseIds) {
                        remainingCourses.add(course)
                    }
                }
                manageSubscriptionsAdapter.notifyDataSetChanged()
                Log.d("MANAGE_SUBSCRIPTIONS_ACTIVITY", "Loaded ${remainingCourses.size} courses")

            }
            .addOnFailureListener { exception ->
                Log.w("MANAGE_SUBS", "Error getting courses", exception)
            }
    }



}

