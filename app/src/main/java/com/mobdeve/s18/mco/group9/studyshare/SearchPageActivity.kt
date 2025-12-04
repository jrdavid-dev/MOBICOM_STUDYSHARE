package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.SearchPageBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class SearchPageActivity : AppCompatActivity() {

    private lateinit var materialAdapter: MaterialAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid
    private lateinit var viewBinding: SearchPageBinding
    private val db = Firebase.firestore

    private var currentSearchText = ""
    private var currentFilterType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = SearchPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (current_user_id == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        setupSearch()
        setupFilterButtons()

        viewBinding.cancelTv.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val query = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)
            .orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter = MaterialAdapter(options, false)

        viewBinding.searchMaterialsRecyclerView.itemAnimator = null
        viewBinding.searchMaterialsRecyclerView.adapter = materialAdapter
        viewBinding.searchMaterialsRecyclerView.layoutManager = LinearLayoutManager(this)
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

    private fun setupFilterButtons() {
        // Show all materials
        viewBinding.searchPageAllBtn.setOnClickListener {
            currentFilterType = null
            updateButtonStates(viewBinding.searchPageAllBtn)
            updateQuery()
        }

        // Show only notes
        viewBinding.searchPageLectureNotesBtn.setOnClickListener {
            currentFilterType = "Notes"
            updateButtonStates(viewBinding.searchPageLectureNotesBtn)
            updateQuery()
        }

        // Show only handouts
        viewBinding.searchPageHandoutsBtn.setOnClickListener {
            currentFilterType = "Handouts"
            updateButtonStates(viewBinding.searchPageHandoutsBtn)
            updateQuery()
        }

        // Show only reviewers
        viewBinding.searchPageReviewersBtn.setOnClickListener {
            currentFilterType = "Reviewers"
            updateButtonStates(viewBinding.searchPageReviewersBtn)
            updateQuery()
        }
    }

    private fun updateButtonStates(selectedButton: View) {
        // Reset all buttons to unselected state
        viewBinding.searchPageAllBtn.isSelected = false
        viewBinding.searchPageLectureNotesBtn.isSelected = false
        viewBinding.searchPageHandoutsBtn.isSelected = false
        viewBinding.searchPageReviewersBtn.isSelected = false

        viewBinding.searchPageAllBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
        viewBinding.searchPageLectureNotesBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
        viewBinding.searchPageHandoutsBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))
        viewBinding.searchPageReviewersBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F8FAFC"))

        viewBinding.searchPageAllBtn.setTextColor(Color.parseColor("#600F172A"))
        viewBinding.searchPageLectureNotesBtn.setTextColor(Color.parseColor("#600F172A"))
        viewBinding.searchPageHandoutsBtn.setTextColor(Color.parseColor("#600F172A"))
        viewBinding.searchPageReviewersBtn.setTextColor(Color.parseColor("#600F172A"))


        selectedButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0891B2"))
        (selectedButton as TextView).setTextColor(Color.parseColor("#FFFFFF"))
        selectedButton.isSelected = true
    }

    private fun updateQuery() {
        materialAdapter.stopListening()

        var query : Query = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION)


        if (currentFilterType != null) {
            query = query.whereEqualTo(MyFirestoreReferences.MATERIAL_TYPE_FIELD, currentFilterType)
        }


        if (currentSearchText.isNotEmpty()) {
            query = query.orderBy(MyFirestoreReferences.MATERIAL_NAME_FIELD)
                .startAt(currentSearchText)
                .endAt(currentSearchText + "\uf8ff")
        } else {
            query = query.orderBy(MyFirestoreReferences.CREATED_AT_FIELD, Query.Direction.DESCENDING)
        }

        val newOptions = FirestoreRecyclerOptions.Builder<Material>()
            .setQuery(query, Material::class.java)
            .build()

        materialAdapter.updateOptions(newOptions)
        materialAdapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        materialAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        materialAdapter.stopListening()
    }
}