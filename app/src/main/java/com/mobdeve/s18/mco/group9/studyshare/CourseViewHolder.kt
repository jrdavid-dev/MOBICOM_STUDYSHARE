package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class CourseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val courseNameTv: TextView = itemView.findViewById(R.id.courseNameTv)
    private val nTotalTv: TextView = itemView.findViewById(R.id.nTotalTv)

    fun bindData(course: Course) {

        courseNameTv.text = course.courseName
        nTotalTv.text = "${course.nTotal} Materials"

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
            // TODO : add IntentPutExtra to populate CourseDetails
            itemView.context.startActivity(intent)
        }
    }
}
