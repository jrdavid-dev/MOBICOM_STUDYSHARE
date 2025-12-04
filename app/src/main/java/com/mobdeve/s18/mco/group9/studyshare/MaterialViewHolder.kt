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
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import java.util.Date


class MaterialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val uploadTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val uploadTypeTv: TextView = itemView.findViewById(R.id.uploadTypeTv)
    private val uploadDateTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val uploadAuthorTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val colorMaterialFrame: FrameLayout = itemView.findViewById(R.id.colorMaterialFrame)

    private val editBtn : ImageView = itemView.findViewById(R.id.materialEditBtn)
    private val deleteBtn : ImageView = itemView.findViewById(R.id.materialDeleteBtn)
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid


    //TODO add username or query
    private var current_user_name: String = "Unknown User"

    fun bindData(material: Material, showEditDelete : Boolean) {


        uploadTitleTv.text = material.materialName
        uploadTypeTv.text = material.materialType.toString()
        uploadDateTv.text = getTimeAgo(material.createdAt)
        uploadAuthorTv.text = "by ${material.materialAuthor}"
        colorMaterialFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(material.colorIcon))

        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(current_user_id.toString())
            .get()
            .addOnSuccessListener { userDocument ->
                val firstName = userDocument.getString(MyFirestoreReferences.FIRST_NAME_FIELD) ?: ""
                val lastName = userDocument.getString(MyFirestoreReferences.LAST_NAME_FIELD) ?: ""
                current_user_name = "$firstName $lastName".trim()
                Log.d("PROFILE_VIEWHOLDER", "Fetched user name: $current_user_name")

                if(showEditDelete){
                    editBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE

                    editBtn.setOnClickListener {
                        showEditMaterialDialog(material)
                    }

                    deleteBtn.setOnClickListener {
                        showDeleteConfirmationDialog(material)
                    }
                } else {
                    editBtn.visibility = View.GONE
                    deleteBtn.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PROFILE_VIEWHOLDER", "Error fetching user", exception)
            }


        colorMaterialFrame.setOnClickListener {
            val intent = Intent(itemView.context, MaterialDetailsActivity::class.java)
            intent.putExtra(IntentKeys.MATERIAL_NAME.name, material.materialName)
            intent.putExtra(IntentKeys.MATERIAL_TYPE.name, material.materialType.toString())
            intent.putExtra(IntentKeys.MATERIAL_TOPIC.name, material.materialTopic)
            intent.putExtra(IntentKeys.MATERIAL_AUTHOR.name, material.materialAuthor)
            intent.putExtra(IntentKeys.MATERIAL_DATE.name, material.createdAt.toString())
            intent.putExtra(IntentKeys.COLOR_ICON.name, material.colorIcon)
            intent.putExtra(IntentKeys.FILE_URL.name, material.fileUrl)
            intent.putExtra(IntentKeys.FILE_NAME.name, material.fileName)
            itemView.context.startActivity(intent)
        }


    }

    private fun showEditMaterialDialog(material: Material) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Edit Material Name")

        val input = EditText(itemView.context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(material.materialName)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Save") { dialog, which ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {

                val db = Firebase.firestore

                val coursesRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION).document(material.id)
                coursesRef.update(MyFirestoreReferences.MATERIAL_NAME_FIELD,newName)
                    .addOnSuccessListener {
                        addEditNotification(material.courseId, material.id, material.materialName)
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

    private fun showDeleteConfirmationDialog(material: Material) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Delete Material")
            .setMessage("Are you sure you want to delete this material?")
            .setPositiveButton("Delete") { dialog, which ->
                val db = Firebase.firestore

                db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                    .document(material.courseId)
                    .update(
                        MyFirestoreReferences.MATERIAL_COUNT_FIELD, FieldValue.increment(-1),
                        MyFirestoreReferences.UPDATED_AT_FIELD, FieldValue.serverTimestamp()
                    )
                    .addOnSuccessListener {
                        Log.d("DELETE_MATERIAL", "Course material count decremented")

                        // Then delete the material
                        val materialRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION).document(material.id)
                        materialRef.delete()
                            .addOnSuccessListener {
                                Log.d("DELETE_MATERIAL", "Material deleted successfully")

                                addDeleteNotification(material.courseId, material.id, material.materialName)
                                Toast.makeText(itemView.context, "Material deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("DELETE_MATERIAL", "Error deleting material", exception)
                                Toast.makeText(itemView.context, "Failed to delete material", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("DELETE_MATERIAL", "Error updating course material count", exception)
                        Toast.makeText(itemView.context, "Failed to update course", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addEditNotification(courseId : String, materialId : String, materialName : String){
        val db = Firebase.firestore
        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId) // Query by course ID
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscribedUserIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.USER_ID_FIELD) // Get user IDs
                }

                Log.d("MANAGE_SUBS", "Found ${subscribedUserIds.size} subscribed users")

                subscribedUserIds.forEach { userId ->

                    if (userId != current_user_id) {
                        val notifsRef = db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION).document()
                        val notifId = notifsRef.id

                        val type = "MATERIAL_EDIT"
                        val notifData = hashMapOf(
                            MyFirestoreReferences.ID_FIELD to notifId,
                            MyFirestoreReferences.USER_ID_FIELD to userId,
                            MyFirestoreReferences.COURSE_ID_FIELD to courseId,
                            MyFirestoreReferences.TYPE_FIELD to type,
                            MyFirestoreReferences.MATERIAL_ID_FIELD to materialId,
                            MyFirestoreReferences.MATERIAL_NAME_FIELD to materialName,
                            MyFirestoreReferences.AUTHOR_NAME_FIELD to current_user_name,
                            MyFirestoreReferences.IS_READ_FIELD to false,
                            MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp()
                        )

                        notifsRef.set(notifData)
                            .addOnSuccessListener {
                                Log.d("NOTIFICATIONS", "Notification sent to user: $userId")
                            }
                            .addOnFailureListener { exception ->
                                Log.w("NOTIFICATIONS", "Error adding notification for user: $userId", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MANAGE_SUBS", "Error getting subscriptions", exception)
            }
    }

    private fun addDeleteNotification(courseId : String, materialId : String, materialName : String){
        val db = Firebase.firestore
        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, courseId)
            .get()
            .addOnSuccessListener { subscriptions ->

                val subscribedUserIds = subscriptions.documents.mapNotNull { subscription ->
                    subscription.getString(MyFirestoreReferences.USER_ID_FIELD)
                }

                Log.d("MANAGE_SUBS", "Found ${subscribedUserIds.size} subscribed users")

                subscribedUserIds.forEach { userId ->

                    if (userId != current_user_id) {
                        val notifsRef = db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION).document()
                        val notifId = notifsRef.id

                        val type = "MATERIAL_DELETE"
                        val notifData = hashMapOf(
                            MyFirestoreReferences.ID_FIELD to notifId,
                            MyFirestoreReferences.USER_ID_FIELD to userId,
                            MyFirestoreReferences.COURSE_ID_FIELD to courseId,
                            MyFirestoreReferences.TYPE_FIELD to type,
                            MyFirestoreReferences.MATERIAL_ID_FIELD to materialId,
                            MyFirestoreReferences.MATERIAL_NAME_FIELD to materialName,
                            MyFirestoreReferences.AUTHOR_NAME_FIELD to current_user_name,
                            MyFirestoreReferences.IS_READ_FIELD to false,
                            MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp()
                        )

                        notifsRef.set(notifData)
                            .addOnSuccessListener {
                                Log.d("NOTIFICATIONS", "Notification sent to user: $userId")
                            }
                            .addOnFailureListener { exception ->
                                Log.w("NOTIFICATIONS", "Error adding notification for user: $userId", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MANAGE_SUBS", "Error getting subscriptions", exception)
            }
    }


}

fun getTimeAgo(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> android.text.format.DateFormat.format("MMM dd, yyyy", date).toString()
    }
}