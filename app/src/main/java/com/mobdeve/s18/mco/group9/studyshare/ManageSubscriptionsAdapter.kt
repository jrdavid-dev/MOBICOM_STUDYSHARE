package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ManageSubscriptionsAdapter(private val data: ArrayList<Course>): RecyclerView.Adapter<ManageSubscriptionsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSubscriptionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.courses_layout, parent, false)
        return ManageSubscriptionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageSubscriptionsViewHolder, position: Int) {
        holder.bindData(data.get(position))

    }

    override fun getItemCount(): Int {
        return data.size
    }
}