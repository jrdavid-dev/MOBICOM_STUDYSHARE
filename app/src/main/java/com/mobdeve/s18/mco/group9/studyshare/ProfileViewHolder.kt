package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mobdeve.s18.mco.group9.studyshare.models.Course
import org.w3c.dom.Text
import java.util.Date


class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val courseTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val courseMaterialCountTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val courseDetailsAuthorTv: TextView = itemView.findViewById(R.id.profileAuthorTv)
    fun bindData(course: Course) {

        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseDetailsAuthorTv.text = course.courseAuthor
        courseMaterialCountTv.text = "${course.materialCount} Materials"

        itemView.setOnClickListener {
                val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
                intent.putExtra(IntentKeys.COURSE_ID.name, course.id.toString())
                intent.putExtra(IntentKeys.COURSE_NAME.name, course.courseName)
                intent.putExtra(IntentKeys.MATERIAL_COUNT.name, course.materialCount.toString())
                intent.putExtra(IntentKeys.LAST_UPDATED.name, getTimeAgo(course.updatedAt))
                intent.putExtra(IntentKeys.COLOR_ICON.name, course.colorIcon)
                itemView.context.startActivity(intent)
        }

    }


}
