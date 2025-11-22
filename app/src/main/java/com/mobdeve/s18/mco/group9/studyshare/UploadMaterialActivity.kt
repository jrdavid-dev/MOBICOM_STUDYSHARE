package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.SearchPageBinding
import com.mobdeve.s18.mco.group9.studyshare.databinding.UploadMaterialBinding

class UploadMaterialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: UploadMaterialBinding = UploadMaterialBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val materialTypes = arrayOf("Lecture Notes", "Handouts", "Reviewers")

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


        val existingCourses = arrayOf(
            "MOBDEVE",
            "CSADPRG",
            "LBYARCH",
            "STADVDB"
        )

        val courseOptions = arrayOf("+ Create New Course") + existingCourses

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
                            // User selected "Create New Course"
                            showCreateNewCourseDialog(viewBinding)
                        } else {
                            // User selected an existing course
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

    private fun showCreateNewCourseDialog(viewBinding: UploadMaterialBinding) {
        val input = EditText(this)
        input.hint = "Enter course name"
        input.setPadding(50, 40, 50, 40)

        AlertDialog.Builder(this)
            .setTitle("Create New Course")
            .setView(input)
            .setPositiveButton("Create") { dialog, _ ->
                val newCourseName = input.text.toString().trim()
                if (newCourseName.isNotEmpty()) {
                    viewBinding.uploadMaterialCourseNameTv.setText(newCourseName)
                    viewBinding.uploadMaterialCourseNameTv.setTextColor(getColor(android.R.color.black))
                    // TODO: Save the new course to your database/list
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}