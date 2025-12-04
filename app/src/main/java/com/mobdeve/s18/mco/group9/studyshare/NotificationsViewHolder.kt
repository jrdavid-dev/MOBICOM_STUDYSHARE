package com.mobdeve.s18.mco.group9.studyshare


import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

import com.mobdeve.s18.mco.group9.studyshare.models.Notification


class NotificationsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val notifTypeNameTv: TextView = itemView.findViewById(R.id.notifTypeNameTv)
    private val notifNameTv: TextView = itemView.findViewById(R.id.notifNameTv)
    private val notifMessageTv: TextView = itemView.findViewById(R.id.notifMessageTv)
    private val notifCreatedAtTv: TextView = itemView.findViewById(R.id.notifCreatedAtTv)

    private val db = Firebase.firestore
    private var courseName: String = "Unknown Course"


    fun bindData(notification : Notification) {

        notifCreatedAtTv.text = getTimeAgo(notification.createdAt)

        db.collection(MyFirestoreReferences.COURSES_COLLECTION)
            .document(notification.courseId)
            .get()
            .addOnSuccessListener { courseDocument ->
                courseName = courseDocument.getString(MyFirestoreReferences.COURSE_NAME_FIELD) ?: "Unknown Course"
            }
            .addOnFailureListener { exception ->
                Log.e("NOTIFICATION", "Error fetching course", exception)
            }

        when(notification.type.toString()) {
            "MATERIAL_UPLOAD" -> {
                notifNameTv.text = courseName
                notifTypeNameTv.text = "New Material Uploaded"
                notifMessageTv.text = notification.materialName
            }

            "MATERIAL_EDIT" -> {
                notifTypeNameTv.text = "Material Updated"
                notifNameTv.text = notification.materialName
                notifMessageTv.text = "The material has been updated"
            }

            "MATERIAL_DELETE" -> {
                notifTypeNameTv.text = "Material Removed"
                notifNameTv.text = notification.materialName
                notifMessageTv.text = "The material has been deleted"
            }

            "COURSE_EDIT" -> {
                notifTypeNameTv.text = "Course Updated"
                notifNameTv.text = courseName
                notifMessageTv.text = "The course has been updated"
            }

            "COURSE_DELETE" -> {
                notifTypeNameTv.text = "Course Removed"
                notifNameTv.text = courseName
                notifMessageTv.text = "The course has been deleted"
            }
        }
    }
}