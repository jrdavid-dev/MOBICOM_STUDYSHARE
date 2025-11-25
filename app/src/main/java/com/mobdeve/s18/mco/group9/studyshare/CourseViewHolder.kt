package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mobdeve.s18.mco.group9.studyshare.models.Course


class CourseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val courseNameTv: TextView = itemView.findViewById(R.id.courseNameTv)
    private val nTotalTv: TextView = itemView.findViewById(R.id.nTotalTv)
    private val colorSubsFrame: FrameLayout = itemView.findViewById(R.id.colorSubsFrame)

    fun bindData(course: Course) {

        courseNameTv.text = course.courseName
        nTotalTv.text = "${course.materialCount} Materials"
        colorSubsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(course.colorIcon))


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

