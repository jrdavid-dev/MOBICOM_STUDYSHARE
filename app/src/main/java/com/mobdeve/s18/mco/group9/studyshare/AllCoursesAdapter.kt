package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AllCoursesAdapter(private val data: ArrayList<Course>): RecyclerView.Adapter<AllCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCoursesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.courses_layout, parent, false)
        return AllCoursesViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllCoursesViewHolder, position: Int) {
        holder.bindData(data.get(position))

    }

    override fun getItemCount(): Int {
        return data.size
    }
}