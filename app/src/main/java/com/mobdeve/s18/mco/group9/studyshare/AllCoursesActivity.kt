package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.AllCoursesBinding

class AllCoursesActivity : AppCompatActivity() {

    private val course : ArrayList<Course> = DataGenerator.generateCourse()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: AllCoursesBinding = AllCoursesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.allCoursesRecyclerView.adapter = AllCoursesAdapter(this.course)
        viewBinding.allCoursesRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}

