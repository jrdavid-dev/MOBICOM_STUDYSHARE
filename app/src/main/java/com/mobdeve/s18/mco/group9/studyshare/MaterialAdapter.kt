package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import androidx.recyclerview.widget.RecyclerView


class MaterialAdapter(
    options: FirestoreRecyclerOptions<Material>,
    private val showEditDelete: Boolean ): FirestoreRecyclerAdapter<Material, MaterialViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.material_layout, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int, model: Material) {
        holder.bindData(model, showEditDelete)

    }


}