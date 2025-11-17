package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.SearchPageBinding

class SearchPageActivity : AppCompatActivity() {

    private val material : ArrayList<Material> = DataGenerator.generateUpload()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: SearchPageBinding = SearchPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.searchMaterialsRecyclerView.adapter = MaterialAdapter(this.material)
        viewBinding.searchMaterialsRecyclerView.layoutManager = LinearLayoutManager(this)

        viewBinding.cancelTv.setOnClickListener(View.OnClickListener{
            val intent = Intent(applicationContext, MainActivity::class.java)
            this.startActivity(intent)
        })
    }
}

