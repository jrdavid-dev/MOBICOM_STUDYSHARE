package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MaterialAdapter(private val data: ArrayList<Material>): RecyclerView.Adapter<MaterialViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.material_layout, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bindData(data.get(position))

    }

    override fun getItemCount(): Int {
        return data.size
    }
}