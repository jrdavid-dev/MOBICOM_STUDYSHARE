package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mobdeve.s18.mco.group9.studyshare.models.Course


class ManageSubscriptionsViewHolder(itemView: View,
    private val onSubscribeToggle: (Course, Boolean, Int) -> Unit): RecyclerView.ViewHolder(itemView) {

    private val courseTitleTv: TextView = itemView.findViewById(R.id.courseTitleTv)
    private val courseMaterialCountTv: TextView = itemView.findViewById(R.id.courseMaterialCountTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.courseDetailsTv)
    private val courseDetailsAuthorTv: TextView = itemView.findViewById(R.id.courseDetailsAuthorTv)
    private val subscribeBtn: TextView = itemView.findViewById(R.id.subscribeBtn)
    private val colorManageSubsFrame: FrameLayout = itemView.findViewById(R.id.colorManageSubsFrame)

    fun bindData(course: Course, isSubscribed: Boolean) {
        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseDetailsAuthorTv.text = course.courseAuthor
        courseMaterialCountTv.text = "${course.materialCount} Materials"
        colorManageSubsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(course.colorIcon))

        updateSubscribeButton(isSubscribed)

        colorManageSubsFrame.setOnClickListener {
            val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
            intent.putExtra(IntentKeys.COURSE_ID.name, course.id.toString())
            intent.putExtra(IntentKeys.COURSE_NAME.name, course.courseName)
            intent.putExtra(IntentKeys.MATERIAL_COUNT.name, course.materialCount.toString())
            intent.putExtra(IntentKeys.LAST_UPDATED.name, getTimeAgo(course.updatedAt))
            intent.putExtra(IntentKeys.COLOR_ICON.name, course.colorIcon)
            itemView.context.startActivity(intent)
        }

        subscribeBtn.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onSubscribeToggle(course, isSubscribed, position)
            }
        }
    }

    private fun updateSubscribeButton(isSubscribed: Boolean) {
        if (isSubscribed) {
            subscribeBtn.text = "Unsubscribe"
            subscribeBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0891B2"))
            subscribeBtn.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            subscribeBtn.text = "Subscribe"
            subscribeBtn.backgroundTintList = null
            subscribeBtn.setTextColor(Color.parseColor("#000000"))
        }
    }
}
