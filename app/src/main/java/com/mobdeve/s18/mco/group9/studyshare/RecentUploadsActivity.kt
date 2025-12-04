package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
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
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: RecentUploadsBinding = RecentUploadsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        if (current_user_id == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val db = Firebase.firestore

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val query = materialsRef.orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, false)


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

