package com.mobdeve.s18.mco.group9.studyshare

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mobdeve.s18.mco.group9.studyshare.models.Notification


class NotificationsAdapter(
    options: FirestoreRecyclerOptions<Notification>): FirestoreRecyclerAdapter<Notification, NotificationsViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.notification_layout, parent, false)
        return NotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int, model: Notification) {
        holder.bindData(model)

    }


}