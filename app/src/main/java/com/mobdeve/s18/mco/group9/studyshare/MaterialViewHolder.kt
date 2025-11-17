package com.mobdeve.s18.mco.group9.studyshare

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView


class MaterialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val uploadTitleTv: TextView = itemView.findViewById(R.id.courseTitleTv)
    private val uploadTypeTv: TextView = itemView.findViewById(R.id.uploadTypeTv)
    private val uploadDateTv: TextView = itemView.findViewById(R.id.courseDetailsTv)
    private val uploadAuthorTv: TextView = itemView.findViewById(R.id.courseTotalTv)


    fun bindData(material: Material) {
        uploadTitleTv.text = material.title
        uploadTypeTv.text = material.type
        uploadDateTv.text = material.date
        uploadAuthorTv.text = "by ${material.author}"
    }
}