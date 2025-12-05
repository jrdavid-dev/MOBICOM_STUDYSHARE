package com.mobdeve.s18.mco.group9.studyshare


import android.graphics.Typeface
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
    private val unreadIndicator: View = itemView.findViewById(R.id.unreadIndicator)
    private val notifRecyclerView: View = itemView.findViewById(R.id.notifRecyclerView)

    private val db = Firebase.firestore
    private var courseName: String = "Unknown Course"


    fun bindData(notification : Notification) {

        notifCreatedAtTv.text = getTimeAgo(notification.createdAt)

        // Log to debug
        Log.d("NOTIFICATION_READ", "Notification ID: ${notification.id}, isRead: ${notification.isRead}")

        // Update UI based on read status
        updateReadStatus(notification.isRead)

        // Set click listener to mark as read
        itemView.setOnClickListener {
            Log.d("NOTIFICATION_CLICK", "Clicked notification, current isRead: ${notification.isRead}")
            if (!notification.isRead) {
                markAsRead(notification)
            }
        }


        when(notification.type.toString()) {
            "MATERIAL_UPLOAD", "COURSE_EDIT", "COURSE_DELETE" -> {

                db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                    .document(notification.courseId)
                    .get()
                    .addOnSuccessListener { courseDocument ->
                        courseName = courseDocument.getString(MyFirestoreReferences.COURSE_NAME_FIELD) ?: "Unknown Course"

                        updateNotificationUI(notification)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("NOTIFICATION", "Error fetching course", exception)
                        courseName = "Unknown Course"

                        updateNotificationUI(notification)
                    }
            }

            else -> {
                updateNotificationUI(notification)
            }
        }
    }

    private fun updateNotificationUI(notification: Notification) {
        when(notification.type.toString()) {
            "MATERIAL_UPLOAD" -> {
                notifTypeNameTv.text = "New Material Uploaded"
                notifNameTv.text = courseName
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

    private fun updateReadStatus(isRead: Boolean) {
        Log.d("NOTIFICATION_STATUS", "Updating read status to: $isRead")

        if (isRead) {
            unreadIndicator.visibility = View.GONE
            notifRecyclerView.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
            notifTypeNameTv.setTypeface(null, Typeface.NORMAL)
            Log.d("NOTIFICATION_STATUS", "Set to READ appearance (white background)")
        } else {
            // Unread notification - highlighted appearance
            unreadIndicator.visibility = View.VISIBLE
            notifRecyclerView.setBackgroundColor(android.graphics.Color.parseColor("#EFF6FF"))
            notifTypeNameTv.setTypeface(null, Typeface.BOLD)
            Log.d("NOTIFICATION_STATUS", "Set to UNREAD appearance (blue background)")
        }
    }

    private fun markAsRead(notification: Notification) {
        Log.d("NOTIFICATION_MARK", "Attempting to mark notification ${notification.id} as read")

        db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION)
            .document(notification.id)
            .update(MyFirestoreReferences.IS_READ_FIELD, true)
            .addOnSuccessListener {
                Log.d("NOTIFICATION_MARK", "✅ Successfully marked as read in Firestore")
                notification.isRead = true
                updateReadStatus(true)
            }
            .addOnFailureListener { exception ->
                Log.e("NOTIFICATION_MARK", "❌ Error marking notification as read", exception)
            }
    }
}