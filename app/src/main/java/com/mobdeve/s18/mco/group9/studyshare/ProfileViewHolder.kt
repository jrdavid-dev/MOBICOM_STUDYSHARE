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
import com.google.firebase.Firebase
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

    fun bindData(course: Course) {

        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseDetailsAuthorTv.text = course.courseAuthor
        courseMaterialCountTv.text = "${course.materialCount} Materials"

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
                    .addOnSuccessListener {  }
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
                // You'll need to handle deletion through the adapter
                // Pass this action to your adapter via a callback

                val db = Firebase.firestore

                val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION).document(course.id)
                coursesRef.delete()
                    .addOnSuccessListener {  }
                    .addOnFailureListener { exception ->
                        Log.w("CHANGE_ME", "Error getting courses", exception)
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}