package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.RecentUploadsBinding

class RecentUploadsActivity : AppCompatActivity() {

    private val material : ArrayList<Material> = DataGenerator.generateUpload()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: RecentUploadsBinding = RecentUploadsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.allCoursesRecyclerView.adapter = MaterialAdapter(this.material)
        viewBinding.allCoursesRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}

