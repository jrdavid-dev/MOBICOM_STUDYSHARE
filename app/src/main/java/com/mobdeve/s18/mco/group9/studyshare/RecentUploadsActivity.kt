package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.RecentUploadsBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material

class RecentUploadsActivity : AppCompatActivity() {

    private lateinit var materialAdapter: MaterialAdapter
    private val current_user_id = "1001"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: RecentUploadsBinding = RecentUploadsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.firestore

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val query = materialsRef.orderBy("createdAt", Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, current_user_id, false)


        viewBinding.recentUploadsRecyclerView.itemAnimator = null
        viewBinding.recentUploadsRecyclerView.adapter = materialAdapter
        viewBinding.recentUploadsRecyclerView.layoutManager = LinearLayoutManager(this)

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

