package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.UploadMaterialBinding
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.storage.storage
import java.io.File
import java.util.Date


class UploadMaterialActivity : AppCompatActivity() {

    private val current_user_id = "1001"
    private val current_user_name = "John Doe"

    private var selectedFileUri: Uri? = null
    private var selectedFileName: String? = null
    private var selectedFileSize: Long? = null

    val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = getFileName(it)
            selectedFileSize = getFileSize(it)

            //TODO  Update UI to show selected file

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        val viewBinding: UploadMaterialBinding = UploadMaterialBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        viewBinding.fileBtn.setOnClickListener {

            filePickerLauncher.launch("*/*")
        }


        val materialTypes = arrayOf("Notes", "Handouts", "Reviewers")

        var selectedPosition = -1

        viewBinding.uploadMaterialTypeTv.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Material Type")
                .setSingleChoiceItems(materialTypes, selectedPosition) { dialog, which ->
                    selectedPosition = which
                }
                .setPositiveButton("OK") { dialog, _ ->
                    if (selectedPosition >= 0) {
                        viewBinding.uploadMaterialTypeTv.text = materialTypes[selectedPosition]
                        viewBinding.uploadMaterialTypeTv.setTextColor(getColor(android.R.color.black))
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val db = Firebase.firestore


        val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
        val coursesQuery = coursesRef.whereEqualTo(MyFirestoreReferences.COURSE_AUTHOR_FIELD, current_user_id)

        val existingCourses = mutableMapOf<String, String>()

        coursesQuery.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val courseId = document.id
                    val courseName = document.getString("courseName").orEmpty()

                    if (courseName.isNotEmpty()) {
                        existingCourses[courseId] = courseName
                    }
                }

                val existingCourseNames = existingCourses.values.toTypedArray()
                val courseOptions = arrayOf("+ Create New Course") + existingCourseNames

                var selectedCoursePosition = -1

                viewBinding.uploadMaterialCourseNameTv.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Select Course")
                        .setSingleChoiceItems(courseOptions, selectedCoursePosition) { dialog, which ->
                            selectedCoursePosition = which
                        }
                        .setPositiveButton("OK") { dialog, _ ->
                            if (selectedCoursePosition >= 0) {
                                if (selectedCoursePosition == 0) {
                                    showCreateNewCourseDialog(viewBinding)
                                } else {
                                    val courseName = courseOptions[selectedCoursePosition]


                                    viewBinding.uploadMaterialCourseNameTv.setText(courseName)
                                    viewBinding.uploadMaterialCourseNameTv.setTextColor(getColor(android.R.color.black))
                                }
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("CHANGE_ME", "Error getting courses", exception)
            }



        // COLOR ICON
        // Define color options with names and resources
        val colorNames = arrayOf("Dark Blue", "Green", "Cyan", "Purple")
        val colorResources = arrayOf(
            R.color.course_dark_blue,
            R.color.course_green,
            R.color.course_cyan,
            R.color.course_purple
        )


        var selectedColorPosition = -1
        viewBinding.uploadMaterialColorIconEv.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Select Color")
                .setSingleChoiceItems(colorNames, selectedColorPosition) { dialog, which ->
                    selectedColorPosition = which
                }
                .setPositiveButton("OK") { dialog, _ ->
                    if (selectedColorPosition >= 0) {
                        val colorName = colorNames[selectedColorPosition]
                        val colorRes = colorResources[selectedColorPosition]

                        viewBinding.uploadMaterialColorIconEv.text = colorName
                        viewBinding.uploadMaterialColorIconEv.setTextColor(getColor(android.R.color.black))

                        // Optional: Add a colored circle icon beside the text
                        val iconDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(getColor(colorRes))
                            setSize(40, 40)
                        }
                        viewBinding.uploadMaterialColorIconEv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            iconDrawable, null,
                            resources.getDrawable(android.R.drawable.arrow_down_float, null),
                            null
                        )
                        viewBinding.uploadMaterialColorIconEv.compoundDrawablePadding = 16
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        viewBinding.uploadBtn.setOnClickListener {
            // PARAMETERS
            val materialName = viewBinding.uploadMaterialTitleEv.text.toString()
            val materialDescription = viewBinding.uploadMaterialDescriptionEV.text.toString()
            val materialTopic = viewBinding.uploadMaterialTopicEv.text.toString()
            val colorIcon = String.format("#%06X", 0xFFFFFF and getColor(colorResources[selectedColorPosition]))

            val courseName = viewBinding.uploadMaterialCourseNameTv.text.toString()
            val courseId = existingCourses.entries.find { it.value == courseName }?.key
            val materialType = materialTypes[selectedPosition]


            val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION).document()

            val materialId = materialsRef.id

            val storageRef = Firebase.storage.reference
            var fileRef = storageRef.child(MyFirestoreReferences.MATERIALS_COLLECTION + "/$materialId/$selectedFileName")

            val uploadTask = fileRef.putFile(selectedFileUri!!)

            uploadTask.addOnSuccessListener {
                // Get download URL
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val fileUrl = downloadUri.toString()
                    Log.d("UPLOAD", "File uploaded: $fileUrl")

                    val materialData = hashMapOf(
                        MyFirestoreReferences.ID_FIELD to materialId,
                        MyFirestoreReferences.MATERIAL_NAME_FIELD to materialName,
                        MyFirestoreReferences.MATERIAL_DESCRIPTION_FIELD to materialDescription,
                        MyFirestoreReferences.MATERIAL_TOPIC_FIELD to materialTopic,
                        MyFirestoreReferences.MATERIAL_AUTHOR_FIELD to current_user_name,
                        MyFirestoreReferences.COLOR_ICON_FIELD to colorIcon,
                        MyFirestoreReferences.MATERIAL_TYPE_FIELD to materialType,
                        MyFirestoreReferences.FILE_NAME_FIELD to selectedFileName,
                        MyFirestoreReferences.FILE_SIZE_FIELD to selectedFileSize,
                        MyFirestoreReferences.FILE_URL_FIELD to fileUrl,
                        MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp(),
                        MyFirestoreReferences.UPDATED_AT_FIELD to FieldValue.serverTimestamp(),
                        MyFirestoreReferences.COURSE_ID_FIELD to courseId
                    )
                    materialsRef.set(materialData)
                        .addOnSuccessListener {  }
                        .addOnFailureListener { exception ->
                            Log.w("CHANGE_ME", "Error getting courses", exception)
                        }

                    db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                        .document(courseId.toString())
                        .update(MyFirestoreReferences.MATERIAL_COUNT_FIELD, FieldValue.increment(1),
                            MyFirestoreReferences.UPDATED_AT_FIELD, FieldValue.serverTimestamp())
                        .addOnSuccessListener {  }
                        .addOnFailureListener { exception ->
                            Log.w("CHANGE_ME", "Error getting courses", exception)
                        }


                }
            }.addOnFailureListener { exception ->
                Log.e("UPLOAD", "Upload failed: ${exception.message}")
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }



        }

    }

    private fun showCreateNewCourseDialog(viewBinding: UploadMaterialBinding) {
        // Inflate custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_course, null)

        val inputName = dialogView.findViewById<EditText>(R.id.et_course_name)
        val inputDescription = dialogView.findViewById<EditText>(R.id.et_course_description)
        val colorIconsLayout = dialogView.findViewById<LinearLayout>(R.id.ll_color_icons)

        // Define available colors
        val colors = arrayOf(
            R.color.course_dark_blue,
            R.color.course_green,
            R.color.course_cyan,
            R.color.course_purple
        )

        var selectedColor = colors[0] // Default color

        // Create color selection buttons
        colors.forEach { colorRes ->
            val colorButton = View(this).apply {
                val size = 120 // Size in pixels
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = 24
                }

                // Create circular background
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(getColor(colorRes))
                }
                background = drawable

                // Set initial alpha (first one selected by default)
                alpha = if (colorRes == selectedColor) 1.0f else 0.5f

                // Add elevation/shadow for better visual
                elevation = 8f

                setOnClickListener {
                    selectedColor = colorRes
                    // Update visual selection - dim unselected colors
                    colorIconsLayout.children.forEach { child ->
                        child.alpha = 0.5f
                        child.elevation = 4f
                    }
                    // Highlight selected color
                    this.alpha = 1.0f
                    this.elevation = 12f
                }
            }
            colorIconsLayout.addView(colorButton)
        }

        AlertDialog.Builder(this)
            .setTitle("Create New Course")
            .setView(dialogView)
            .setPositiveButton("Create") { dialog, _ ->
                val courseName = inputName.text.toString().trim()
                val courseDetails = inputDescription.text.toString().trim()

                if (courseName.isNotEmpty()) {

                    // Update UI
                    viewBinding.uploadMaterialCourseNameTv.setText(courseName)
                    viewBinding.uploadMaterialCourseNameTv.setTextColor(getColor(android.R.color.black))


                    val db = Firebase.firestore

                    val newCourseRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION).document()
                    val newCourseId = newCourseRef.id
                    val colorIcon = String.format("#%06X", 0xFFFFFF and getColor(selectedColor))

                    val newCourseData = hashMapOf(
                        MyFirestoreReferences.ID_FIELD to newCourseId,
                        MyFirestoreReferences.COURSE_NAME_FIELD to courseName,
                        MyFirestoreReferences.COURSE_AUTHOR_FIELD to current_user_id,
                        MyFirestoreReferences.COURSE_DETAILS_FIELD to courseDetails,
                        MyFirestoreReferences.COLOR_ICON_FIELD to colorIcon,
                        MyFirestoreReferences.MATERIAL_COUNT_FIELD to 0,
                        MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp(),
                        MyFirestoreReferences.UPDATED_AT_FIELD to FieldValue.serverTimestamp()
                    )

                    newCourseRef.set(newCourseData)
                        .addOnSuccessListener {  }
                        .addOnFailureListener { exception ->
                            Log.w("CHANGE_ME", "Error getting courses", exception)
                        }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    private fun getFileSize(uri: Uri): Long {
        var fileSize = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            fileSize = cursor.getLong(sizeIndex)
        }
        return fileSize
    }



}