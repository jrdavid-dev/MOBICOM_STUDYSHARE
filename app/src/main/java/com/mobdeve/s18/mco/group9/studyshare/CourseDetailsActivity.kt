package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.CourseDetailsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material

class CourseDetailsActivity : AppCompatActivity() {

    private lateinit var materialAdapter : MaterialAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: CourseDetailsBinding = CourseDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (current_user_id == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val courseId = intent.getStringExtra(IntentKeys.COURSE_ID.name)
        val courseName = intent.getStringExtra(IntentKeys.COURSE_NAME.name)
        val materialCount = intent.getStringExtra(IntentKeys.MATERIAL_COUNT.name)
        val lastUpdated = intent.getStringExtra(IntentKeys.LAST_UPDATED.name)
        val colorIcon = intent.getStringExtra(IntentKeys.COLOR_ICON.name)

        viewBinding.courseDetailsTitleTv.text = courseName
        viewBinding.courseDetailsTotalTv.text = "$materialCount Materials"
        viewBinding.courseLastUpdatedTv.text = lastUpdated
        viewBinding.colorCourseDetailsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorIcon))

        val db = Firebase.firestore

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val query = materialsRef
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, true)

        viewBinding.courseDetailsMaterialRecyclerView.itemAnimator = null
        viewBinding.courseDetailsMaterialRecyclerView.adapter = materialAdapter
        viewBinding.courseDetailsMaterialRecyclerView.layoutManager = LinearLayoutManager(this)
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