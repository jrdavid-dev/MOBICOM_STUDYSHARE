package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityMainBinding
import com.mobdeve.s18.mco.group9.studyshare.databinding.CourseDetailsBinding

class CourseDetailsActivity : AppCompatActivity() {

    private val material : ArrayList<Material> = DataGenerator.generateUpload()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: CourseDetailsBinding = CourseDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.courseDetailsMaterialRecyclerView.adapter = MaterialAdapter(this.material)
        viewBinding.courseDetailsMaterialRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}

