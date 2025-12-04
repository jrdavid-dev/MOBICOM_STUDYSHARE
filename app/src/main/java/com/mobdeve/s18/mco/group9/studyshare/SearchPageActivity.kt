package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.SearchPageBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material

class SearchPageActivity : AppCompatActivity() {

    private lateinit var materialAdapter: MaterialAdapter
    private val current_user_id = "1001"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: SearchPageBinding = SearchPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.firestore

        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)

        val query = materialsRef.orderBy("createdAt", Query.Direction.DESCENDING)


        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, current_user_id, false)

        viewBinding.searchMaterialsRecyclerView.itemAnimator = null
        viewBinding.searchMaterialsRecyclerView.adapter = materialAdapter
        viewBinding.searchMaterialsRecyclerView.layoutManager = LinearLayoutManager(this)

        viewBinding.cancelTv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, MainActivity::class.java)
            this.startActivity(intent)
        })
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

