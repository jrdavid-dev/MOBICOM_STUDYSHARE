package com.mobdeve.s18.mco.group9.studyshare

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.models.Course
import org.w3c.dom.Text
import java.util.Date


class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val courseTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val courseMaterialCountTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val courseDetailsAuthorTv: TextView = itemView.findViewById(R.id.profileAuthorTv)

    private val profileCoursesFrame: FrameLayout = itemView.findViewById(R.id.colorManageSubsFrame)

    private val editBtn: ImageView = itemView.findViewById(R.id.profileEditCourseBtn)
    private val deleteBtn: ImageView = itemView.findViewById(R.id.profileCourseDeleteBtn)
    private val colorManageSubsFrame: FrameLayout = itemView.findViewById(R.id.colorManageSubsFrame)

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    private var current_user_name: String = "Unknown User"


    fun bindData(course: Course) {

        val db = Firebase.firestore

        Log.d("PROFILE_VIEWHOLDER", "Fetching user info for courseAuthor: ${course.courseAuthor}")

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(course.courseAuthor)
            .get()
            .addOnSuccessListener { userDocument ->
                val firstName = userDocument.getString(MyFirestoreReferences.FIRST_NAME_FIELD) ?: ""
                val lastName = userDocument.getString(MyFirestoreReferences.LAST_NAME_FIELD) ?: ""
                current_user_name = "$firstName $lastName".trim()
                Log.d("PROFILE_VIEWHOLDER", "Fetched user name: $current_user_name")
                courseDetailsAuthorTv.text = current_user_name
            }
            .addOnFailureListener { exception ->
                Log.e("PROFILE_VIEWHOLDER", "Error fetching user", exception)
            }

        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseMaterialCountTv.text = "${course.materialCount} Materials"
        colorManageSubsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(course.colorIcon))

        profileCoursesFrame.setOnClickListener {
            val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
            intent.putExtra(IntentKeys.COURSE_ID.name, course.id.toString())
            intent.putExtra(IntentKeys.COURSE_NAME.name, course.courseName)
            intent.putExtra(IntentKeys.MATERIAL_COUNT.name, course.materialCount.toString())
            intent.putExtra(IntentKeys.LAST_UPDATED.name, getTimeAgo(course.updatedAt))
            intent.putExtra(IntentKeys.COLOR_ICON.name, course.colorIcon)
            itemView.context.startActivity(intent)
        }

        editBtn.setOnClickListener {
            showEditCourseDialog(course)
        }

        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog(course)
        }
    }

    private fun showEditCourseDialog(course: Course) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Edit Course Name")

        val input = EditText(itemView.context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(course.courseName)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Save") { dialog, which ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {

                val db = Firebase.firestore

                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION).document(course.id)
                coursesRef.update(MyFirestoreReferences.COURSE_NAME_FIELD,newName)
                    .addOnSuccessListener {
                        addEditNotification(course.id)
                    }
                    .addOnFailureListener { exception ->
                        Log.w("CHANGE_ME", "Error getting courses", exception)
                    }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteConfirmationDialog(course: Course) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { dialog, which ->
                val db = Firebase.firestore


                if (course.materialCount > 0) {

                    AlertDialog.Builder(itemView.context)
                        .setTitle("Cannot Delete Course")
                        .setMessage("This course has ${course.materialCount} material(s). Please delete all materials before deleting the course.")
                        .setPositiveButton("OK", null)
                        .show()
                    return@setPositiveButton
                }


                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION).document(course.id)
                coursesRef.delete()
                    .addOnSuccessListener {
                        Log.d("DELETE_COURSE", "Course deleted successfully")
                        addDeleteNotification(course.id)
                        Toast.makeText(itemView.context, "Course deleted successfully", Toast.LENGTH_SHORT).show()

                        deleteSubscriptions(course.id)

                    }
                    .addOnFailureListener { exception ->
                        Log.e("DELETE_COURSE", "Error deleting course", exception)
                        Toast.makeText(itemView.context, "Failed to delete course", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSubscriptions(courseId: String){
        val db = Firebase.firestore
        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId)
            .get()
            .addOnSuccessListener { subscriptions ->
                if (subscriptions.isEmpty) {
                    Log.d("DELETE_COURSE", "No subscriptions found for this course")
                } else {
                    subscriptions.documents.forEach { subscription ->
                        subscription.reference.delete()
                            .addOnSuccessListener {
                                Log.d("DELETE_COURSE", "Subscription ${subscription.id} deleted")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("DELETE_COURSE", "Error deleting subscription", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DELETE_COURSE", "Error querying subscriptions", exception)
            }
    }

    private fun addEditNotification(courseId : String){
        val db = Firebase.firestore
        Log.d("EDIT_NOTIFICATION", "=== Starting addEditNotification ===")
        Log.d("EDIT_NOTIFICATION", "Course ID: $courseId")
        Log.d("EDIT_NOTIFICATION", "Current User ID: $current_user_id")
        Log.d("EDIT_NOTIFICATION", "Current User Name: $current_user_name")

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId)
            .get()
            .addOnSuccessListener { subscriptions ->
                Log.d("EDIT_NOTIFICATION", "Query returned ${subscriptions.size()} subscription documents")

                val subscribedUserIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.USER_ID_FIELD)
                }

                Log.d("EDIT_NOTIFICATION", "Found ${subscribedUserIds.size} subscribed users: $subscribedUserIds")

                subscribedUserIds.forEach { userId ->
                    Log.d("EDIT_NOTIFICATION", "Processing userId: '$userId'")
                    Log.d("EDIT_NOTIFICATION", "Comparing with current_user_id: '$current_user_id'")
                    Log.d("EDIT_NOTIFICATION", "Are they different? ${userId != current_user_id}")

                    if (userId != current_user_id) {
                        Log.d("EDIT_NOTIFICATION", "Sending notification to user: $userId")

                        val notifsRef = db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION).document()
                        val notifId = notifsRef.id

                        val type = "COURSE_EDIT"
                        val notifData = hashMapOf(
                            MyFirestoreReferences.ID_FIELD to notifId,
                            MyFirestoreReferences.USER_ID_FIELD to userId,
                            MyFirestoreReferences.COURSE_ID_FIELD to courseId,
                            MyFirestoreReferences.TYPE_FIELD to type,
                            MyFirestoreReferences.MATERIAL_ID_FIELD to "",
                            MyFirestoreReferences.MATERIAL_NAME_FIELD to "",
                            MyFirestoreReferences.AUTHOR_NAME_FIELD to current_user_name,
                            MyFirestoreReferences.IS_READ_FIELD to false,
                            MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp()
                        )

                        Log.d("EDIT_NOTIFICATION", "Notification data: $notifData")

                        notifsRef.set(notifData)
                            .addOnSuccessListener {
                                Log.d("EDIT_NOTIFICATION", "Notification successfully sent to user: $userId")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("EDIT_NOTIFICATION", "Error sending notification to user: $userId", exception)
                            }
                    } else {
                        Log.d("EDIT_NOTIFICATION", "Skipping current user (author): $userId")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EDIT_NOTIFICATION", "Error getting subscriptions", exception)
            }
    }

    private fun addDeleteNotification(courseId : String){
        val db = Firebase.firestore
        Log.d("DELETE_NOTIFICATION", "=== Starting addDeleteNotification ===")
        Log.d("DELETE_NOTIFICATION", "Course ID: $courseId")
        Log.d("DELETE_NOTIFICATION", "Current User ID: $current_user_id")
        Log.d("DELETE_NOTIFICATION", "Current User Name: $current_user_name")

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId)
            .get()
            .addOnSuccessListener { subscriptions ->
                Log.d("DELETE_NOTIFICATION", "Query returned ${subscriptions.size()} subscription documents")

                val subscribedUserIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.USER_ID_FIELD)
                }

                Log.d("DELETE_NOTIFICATION", "Found ${subscribedUserIds.size} subscribed users: $subscribedUserIds")

                subscribedUserIds.forEach { userId ->
                    Log.d("DELETE_NOTIFICATION", "Processing userId: '$userId'")
                    Log.d("DELETE_NOTIFICATION", "Comparing with current_user_id: '$current_user_id'")
                    Log.d("DELETE_NOTIFICATION", "Are they different? ${userId != current_user_id}")

                    if (userId != current_user_id) {
                        Log.d("DELETE_NOTIFICATION", "Sending notification to user: $userId")

                        val notifsRef = db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION).document()
                        val notifId = notifsRef.id

                        val type = "COURSE_DELETE"
                        val notifData = hashMapOf(
                            MyFirestoreReferences.ID_FIELD to notifId,
                            MyFirestoreReferences.USER_ID_FIELD to userId,
                            MyFirestoreReferences.COURSE_ID_FIELD to courseId,
                            MyFirestoreReferences.TYPE_FIELD to type,
                            MyFirestoreReferences.MATERIAL_ID_FIELD to "",
                            MyFirestoreReferences.MATERIAL_NAME_FIELD to "",
                            MyFirestoreReferences.AUTHOR_NAME_FIELD to current_user_name,
                            MyFirestoreReferences.IS_READ_FIELD to false,
                            MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp()
                        )

                        Log.d("DELETE_NOTIFICATION", "Notification data: $notifData")

                        notifsRef.set(notifData)
                            .addOnSuccessListener {
                                Log.d("DELETE_NOTIFICATION", "Notification successfully sent to user: $userId")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("DELETE_NOTIFICATION", " Error sending notification to user: $userId", exception)
                            }
                    } else {
                        Log.d("DELETE_NOTIFICATION", "Skipping current user (author): $userId")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DELETE_NOTIFICATION", " Error getting subscriptions", exception)
            }
    }
}