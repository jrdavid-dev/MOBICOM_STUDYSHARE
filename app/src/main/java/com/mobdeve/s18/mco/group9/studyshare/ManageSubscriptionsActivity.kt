package com.mobdeve.s18.mco.group9.studyshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.mco.group9.studyshare.databinding.ManageSubscriptionsBinding
import com.mobdeve.s18.mco.group9.studyshare.databinding.MaterialDetailsBinding

class ManageSubscriptionsActivity : AppCompatActivity() {


    private val course : ArrayList<Course> = DataGenerator.generateCourse()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewBinding: ManageSubscriptionsBinding = ManageSubscriptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        viewBinding.manageSubscriptionsRecyclerView.adapter = ManageSubscriptionsAdapter(this.course)
        viewBinding.manageSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}

