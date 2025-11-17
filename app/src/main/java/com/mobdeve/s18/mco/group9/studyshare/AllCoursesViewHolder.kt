package com.mobdeve.s18.mco.group9.studyshare

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class AllCoursesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val courseNameTv: TextView = itemView.findViewById(R.id.courseTitleTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.courseDetailsTv)
    private val courseTotalTv: TextView = itemView.findViewById(R.id.courseTotalTv)

    fun bindData(course: Course) {
        courseNameTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseTotalTv.text = "${course.nTotal} Materials"
    }
}
