package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityMainBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class MainActivity : AppCompatActivity() {


    private lateinit var materialAdapter : MaterialAdapter
    private val current_user_id = "1001"

    companion object {
        val subscribedCourses = ArrayList<Course>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Material Adapter
        val db = Firebase.firestore

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val materialsQuery = materialsRef.orderBy("createdAt", Query.Direction.DESCENDING).limit(10)


        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(materialsQuery, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options)
        viewBinding.uploadsRecyclerView.itemAnimator = null


        viewBinding.uploadsRecyclerView.adapter = materialAdapter
        viewBinding.uploadsRecyclerView.layoutManager = LinearLayoutManager(this)

        // SUBS ADAPTER
        val courseAdapter = CourseAdapter(subscribedCourses)
        viewBinding.subsRecyclerView.adapter = courseAdapter
        viewBinding.subsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        generateSubscribedCourses(courseAdapter)



        viewBinding.searchButton.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, SearchPageActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.searchIv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, SearchPageActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.seeAllUploadsTv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, RecentUploadsActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.fabUploadBtn.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, UploadMaterialActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.manageSubsTv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, ManageSubscriptionsActivity::class.java)
            this.startActivity(intent)
        })


    }

    private fun generateSubscribedCourses(courseAdapter : CourseAdapter){

        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscriptionCourseIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.COURSE_ID_FIELD)
                }


                db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                    .whereIn("id", subscriptionCourseIds.map { it.toLong() })
                    .get()
                    .addOnSuccessListener { sub_courses ->
                        subscribedCourses.clear()

                        for(sub_course in sub_courses.documents){
                            val course = sub_course.toObject(Course::class.java)
                            if(course != null){
                                subscribedCourses.add(course)
                            }
                        }
                        courseAdapter.notifyDataSetChanged()
                        Log.d("MAIN_ACTIVITY", "Loaded ${subscribedCourses.size} courses")
                    }
                    .addOnFailureListener { exception ->
                        Log.w("MAIN_ACTIVITY", "Error getting courses", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("MAIN_ACTIVITY", "Error getting courses", exception)

            }

    }

    override fun onStart() {
        super.onStart()
        this.materialAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        this.materialAdapter.stopListening()
    }



}

