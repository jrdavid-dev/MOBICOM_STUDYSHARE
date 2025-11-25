package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ManageSubscriptionsAdapter(
    options: FirestoreRecyclerOptions<Course>): FirestoreRecyclerAdapter<Course, ManageSubscriptionsViewHolder>(options)  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSubscriptionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.courses_layout, parent, false)
        return ManageSubscriptionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageSubscriptionsViewHolder, position: Int, model :Course) {
        holder.bindData(model)

    }

}