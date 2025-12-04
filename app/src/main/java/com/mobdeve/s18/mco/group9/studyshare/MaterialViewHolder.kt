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

    fun bindData(material: Material, currentUserId : String, showEditDelete : Boolean) {
        uploadTitleTv.text = material.materialName
        uploadTypeTv.text = material.materialType.toString()
        uploadDateTv.text = getTimeAgo(material.createdAt)
        uploadAuthorTv.text = "by ${material.materialAuthor}"
        colorMaterialFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(material.colorIcon))

        val userOwnsMaterial = material.materialAuthor == currentUserId

        if (showEditDelete && userOwnsMaterial) {
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

    private fun showDeleteConfirmationDialog(material: Material) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Delete Material")
            .setMessage("Are you sure you want to delete this material?")
            .setPositiveButton("Delete") { dialog, which ->


                val db = Firebase.firestore

                val coursesRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION).document(material.id)
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
