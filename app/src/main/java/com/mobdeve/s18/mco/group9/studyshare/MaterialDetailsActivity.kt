package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.AllCoursesBinding
import com.mobdeve.s18.mco.group9.studyshare.databinding.MaterialDetailsBinding

class MaterialDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: MaterialDetailsBinding = MaterialDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

    }
}

