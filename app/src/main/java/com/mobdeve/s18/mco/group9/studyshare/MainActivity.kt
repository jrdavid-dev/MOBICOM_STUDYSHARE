package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val course : ArrayList<Course> = DataGenerator.generateCourse()
    private val material : ArrayList<Material> = DataGenerator.generateUpload()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.subsRecyclerView.adapter = CourseAdapter(this.course)
        viewBinding.subsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        viewBinding.uploadsRecyclerView.adapter = MaterialAdapter(this.material)
        viewBinding.uploadsRecyclerView.layoutManager = LinearLayoutManager(this)

        viewBinding.searchButton.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, SearchPageActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.searchIv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, SearchPageActivity::class.java)
            this.startActivity(intent)
        })

        viewBinding.seeAllUploadsTv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, AllCoursesActivity::class.java)
            this.startActivity(intent)
        })



    }
}

