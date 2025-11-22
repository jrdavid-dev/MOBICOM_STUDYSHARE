package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class ManageSubscriptionsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val courseTitleTv: TextView = itemView.findViewById(R.id.courseTitleTv)
    private val courseTotalTv: TextView = itemView.findViewById(R.id.courseTotalTv)

    fun bindData(course: Course) {

        courseTitleTv.text = course.courseName
        courseTotalTv.text = "${course.nTotal} Materials"

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
            // TODO : add IntentPutExtra to populate CourseDetails
            itemView.context.startActivity(intent)
        }
    }
}
