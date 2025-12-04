package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.UploadMaterialBinding
import android.provider.OpenableColumns
import android.widget.Toast
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth

class UploadMaterialActivity : AppCompatActivity() {

    private lateinit var viewBinding: UploadMaterialBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid
    private var current_user_name: String = ""

    private var selectedFileUri: Uri? = null
    private var selectedFileName: String? = null
    private var selectedFileSize: Long? = null
    private var selectedColorPosition = -1
    private var selectedMaterialTypePosition = -1
    private var selectedCourseId: String? = null

    private val existingCourses = mutableMapOf<String, String>()

    private val materialTypes = arrayOf("Notes", "Handouts", "Reviewers")
    private val colorNames = arrayOf("Dark Blue", "Green", "Cyan", "Purple")
    private val colorResources = arrayOf(
        R.color.course_dark_blue,
        R.color.course_green,
        R.color.course_cyan,
        R.color.course_purple
    )

    private val filePickerLauncher = registerForActivityResult(
    ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = getFileName(it)
            selectedFileSize = getFileSize(it)

            viewBinding.fileStatusTv.text = "File Selected ✓"
            viewBinding.fileStatusTv.setTextColor(getColor(R.color.course_green))
            viewBinding.fileInfoTv.visibility = View.GONE
            viewBinding.fileBtn.backgroundTintList = ColorStateList.valueOf(getColor(R.color.course_green))

            viewBinding.fileDetailsCard.visibility = View.VISIBLE
            viewBinding.fileNameDetailTv.text = selectedFileName
            viewBinding.fileSizeTv.text = formatFileSize(selectedFileSize ?: 0)

            viewBinding.removeFileBtn.setOnClickListener {
                clearFileSelection()
            }
        }
    }

    private fun clearFileSelection() {
        selectedFileUri = null
        selectedFileName = null
        selectedFileSize = null

        // Reset UI
        viewBinding.fileStatusTv.text = "Tap to Select File"
        viewBinding.fileStatusTv.setTextColor(Color.parseColor("#6B6D71"))
        viewBinding.fileInfoTv.visibility = View.VISIBLE
        viewBinding.fileBtn.backgroundTintList = ColorStateList.valueOf(getColor(R.color.course_cyan))
        viewBinding.fileDetailsCard.visibility = View.GONE
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format("%.1f KB", size / 1024.0)
            else -> String.format("%.1f MB", size / (1024.0 * 1024.0))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = UploadMaterialBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (current_user_id == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadUserName()
        setupFileSelector()
        setupMaterialTypeSelector()
        loadCoursesAndSetupSelector()
        setupColorIconSelector()
        setupUploadButton()
    }

    private fun loadUserName() {
        Firebase.firestore.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(current_user_id!!)
            .get()
            .addOnSuccessListener { doc ->
                val first = doc.getString(MyFirestoreReferences.FIRSTNAME_FIELD) ?: ""
                val last = doc.getString(MyFirestoreReferences.LASTNAME_FIELD) ?: ""
                current_user_name = "$first $last"
            }
    }

    private fun setupFileSelector() {
        viewBinding.fileBtn.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }
    }

    private fun setupMaterialTypeSelector() {
        viewBinding.uploadMaterialTypeTv.setOnClickListener {
            showMaterialTypeDialog()
        }
    }

    private fun setupColorIconSelector() {
        viewBinding.uploadMaterialColorIconEv.setOnClickListener {
            showColorIconDialog()
        }
    }

    private fun loadCoursesAndSetupSelector() {
        val db = Firebase.firestore
        val coursesRef = db.collection(MyFirestoreReferences.COURSES_COLLECTION)
        val coursesQuery = coursesRef.whereEqualTo(MyFirestoreReferences.COURSE_AUTHOR_FIELD, current_user_id)

        coursesQuery.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val courseId = document.id
                    val courseName = document.getString(MyFirestoreReferences.COURSE_NAME_FIELD).orEmpty()
                    if (courseName.isNotEmpty()) {
                        existingCourses[courseId] = courseName
                    }
                }

                viewBinding.uploadMaterialCourseNameTv.setOnClickListener {
                    showCourseSelectionDialog()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UPLOAD_MATERIAL", "Error loading courses", exception)
                Toast.makeText(this, "Failed to load courses", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showMaterialTypeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Material Type")
            .setSingleChoiceItems(materialTypes, selectedMaterialTypePosition) { dialog, which ->
                selectedMaterialTypePosition = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                if (selectedMaterialTypePosition >= 0) {
                    viewBinding.uploadMaterialTypeTv.setText(materialTypes[selectedMaterialTypePosition])
                    viewBinding.uploadMaterialTypeTv.setTextColor(getColor(android.R.color.black))
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCourseSelectionDialog() {
        val existingCourseNames = existingCourses.values.toTypedArray()
        val courseOptions = arrayOf("+ Create New Course") + existingCourseNames

        var tempSelectedPosition = -1

        AlertDialog.Builder(this)
            .setTitle("Select Course")
            .setSingleChoiceItems(courseOptions, tempSelectedPosition) { dialog, which ->
                tempSelectedPosition = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                if (tempSelectedPosition >= 0) {
                    if (tempSelectedPosition == 0) {
                        showCreateNewCourseDialog()
                    } else {
                        val courseName = courseOptions[tempSelectedPosition]
                        selectedCourseId = existingCourses.entries.find { it.value == courseName }?.key

                        viewBinding.uploadMaterialCourseNameTv.setText(courseName)
                        viewBinding.uploadMaterialCourseNameTv.setTextColor(getColor(android.R.color.black))
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showColorIconDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Color")
            .setSingleChoiceItems(colorNames, selectedColorPosition) { dialog, which ->
                selectedColorPosition = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                if (selectedColorPosition >= 0) {
                    val colorName = colorNames[selectedColorPosition]
                    val colorRes = colorResources[selectedColorPosition]

                    viewBinding.uploadMaterialColorIconEv.setText(colorName)
                    viewBinding.uploadMaterialColorIconEv.setTextColor(getColor(android.R.color.black))

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

    private fun showCreateNewCourseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_course, null)
        val inputName = dialogView.findViewById<EditText>(R.id.et_course_name)
        val inputDescription = dialogView.findViewById<EditText>(R.id.et_course_description)
        val colorIconsLayout = dialogView.findViewById<LinearLayout>(R.id.ll_color_icons)

        val colors = arrayOf(
            R.color.course_dark_blue,
            R.color.course_green,
            R.color.course_cyan,
            R.color.course_purple
        )

        var selectedColor = colors[0]

        colors.forEach { colorRes ->
            val colorButton = View(this).apply {
                val size = 120
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = 24
                }

                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(getColor(colorRes))
                }
                background = drawable
                alpha = if (colorRes == selectedColor) 1.0f else 0.5f
                elevation = 8f

                setOnClickListener {
                    selectedColor = colorRes
                    colorIconsLayout.children.forEach { child ->
                        child.alpha = 0.5f
                        child.elevation = 4f
                    }
                    this.alpha = 1.0f
                    this.elevation = 12f
                }
            }
            colorIconsLayout.addView(colorButton)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Create") { dialog, _ ->
                val courseName = inputName.text.toString().trim()
                val courseDetails = inputDescription.text.toString().trim()

                if (courseName.isEmpty()) {
                    Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                createNewCourse(courseName, courseDetails, selectedColor)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNewCourse(courseName: String, courseDetails: String, selectedColor: Int) {
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
            .addOnSuccessListener {
                // Update local map and UI
                existingCourses[newCourseId] = courseName
                selectedCourseId = newCourseId

                viewBinding.uploadMaterialCourseNameTv.setText(courseName)
                viewBinding.uploadMaterialCourseNameTv.setTextColor(getColor(android.R.color.black))

                Toast.makeText(this, "Course created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.w("UPLOAD_MATERIAL", "Error creating course", exception)
                Toast.makeText(this, "Failed to create course", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupUploadButton() {
        viewBinding.uploadBtn.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun validateAndUpload() {

        val materialName = viewBinding.uploadMaterialTitleEv.text.toString().trim()
        val materialDescription = viewBinding.uploadMaterialDescriptionEV.text.toString().trim()
        val materialTopic = viewBinding.uploadMaterialTopicEv.text.toString().trim()

        // ERROR HANDLING
        when {
            materialName.isEmpty() -> {
                viewBinding.uploadMaterialTitleEv.error = "Material name is required"
                viewBinding.uploadMaterialTitleEv.requestFocus()
                Toast.makeText(this, "Please enter a material name", Toast.LENGTH_SHORT).show()
            }
            materialTopic.isEmpty() -> {
                viewBinding.uploadMaterialTopicEv.error = "Topic is required"
                viewBinding.uploadMaterialTopicEv.requestFocus()
                Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
            }
            selectedCourseId == null -> {
                Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show()
            }
            selectedMaterialTypePosition < 0 -> {
                Toast.makeText(this, "Please select a material type", Toast.LENGTH_SHORT).show()
            }
            selectedColorPosition < 0 -> {
                Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show()
            }
            selectedFileUri == null -> {
                Toast.makeText(this, "Please select a file to upload", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // All validations passed, proceed with upload
                uploadMaterial(materialName, materialDescription, materialTopic)
            }
        }
    }

    private fun uploadMaterial(materialName: String, materialDescription: String, materialTopic: String) {
        val db = Firebase.firestore
        val materialsRef = db.collection(MyFirestoreReferences.MATERIALS_COLLECTION).document()
        val materialId = materialsRef.id

        val colorIcon = String.format("#%06X", 0xFFFFFF and getColor(colorResources[selectedColorPosition]))
        val materialType = materialTypes[selectedMaterialTypePosition]

        // Show loading indicator
        viewBinding.uploadBtn.isEnabled = false
        viewBinding.uploadText.text = "Uploading..."
        viewBinding.uploadIv.setImageResource(R.drawable.load)

        // Upload file to Firebase Storage
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("${MyFirestoreReferences.MATERIALS_COLLECTION}/$materialId/$selectedFileName")

        fileRef.putFile(selectedFileUri!!)
            .addOnSuccessListener {
                // Get download URL
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val fileUrl = downloadUri.toString()

                    // Save material data to Firestore
                    saveMaterialToFirestore(
                        materialsRef,
                        materialId,
                        materialName,
                        materialDescription,
                        materialTopic,
                        colorIcon,
                        materialType,
                        fileUrl
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UPLOAD_MATERIAL", "Upload failed: ${exception.message}")
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()

                // Reset button
                viewBinding.uploadBtn.isEnabled = true
                viewBinding.uploadText.text = "Upload Material"
                viewBinding.uploadIv.setImageResource(android.R.drawable.stat_sys_upload_done)

            }
    }

    private fun saveMaterialToFirestore(
        materialsRef: com.google.firebase.firestore.DocumentReference,
        materialId: String,
        materialName: String,
        materialDescription: String,
        materialTopic: String,
        colorIcon: String,
        materialType: String,
        fileUrl: String
    ) {
        val db = Firebase.firestore

        val materialData = hashMapOf(
            MyFirestoreReferences.ID_FIELD to materialId,
            MyFirestoreReferences.MATERIAL_NAME_FIELD to materialName,
            MyFirestoreReferences.MATERIAL_DESCRIPTION_FIELD to materialDescription,
            MyFirestoreReferences.MATERIAL_TOPIC_FIELD to materialTopic,
            MyFirestoreReferences.MATERIAL_AUTHOR_FIELD to current_user_name,
            MyFirestoreReferences.USER_ID_FIELD to current_user_id,
            MyFirestoreReferences.COLOR_ICON_FIELD to colorIcon,
            MyFirestoreReferences.MATERIAL_TYPE_FIELD to materialType,
            MyFirestoreReferences.FILE_NAME_FIELD to selectedFileName,
            MyFirestoreReferences.FILE_SIZE_FIELD to selectedFileSize,
            MyFirestoreReferences.FILE_URL_FIELD to fileUrl,
            MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp(),
            MyFirestoreReferences.UPDATED_AT_FIELD to FieldValue.serverTimestamp(),
            MyFirestoreReferences.COURSE_ID_FIELD to selectedCourseId
        )

        materialsRef.set(materialData)
            .addOnSuccessListener {
                // Update course material count
                db.collection(MyFirestoreReferences.COURSES_COLLECTION)
                    .document(selectedCourseId!!)
                    .update(
                        MyFirestoreReferences.MATERIAL_COUNT_FIELD, FieldValue.increment(1),
                        MyFirestoreReferences.UPDATED_AT_FIELD, FieldValue.serverTimestamp()
                    )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Material uploaded successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Close activity after successful upload
                    }
                    .addOnFailureListener { exception ->
                        Log.w("UPLOAD_MATERIAL", "Error updating course", exception)
                        Toast.makeText(this, "Material uploaded but failed to update course", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("UPLOAD_MATERIAL", "Error saving material", exception)
                Toast.makeText(this, "Failed to save material data", Toast.LENGTH_SHORT).show()

                // Reset button
                viewBinding.uploadBtn.isEnabled = true
                viewBinding.uploadText.text = "Upload"
                viewBinding.uploadIv.setImageResource(android.R.drawable.stat_sys_upload_done)
            }
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