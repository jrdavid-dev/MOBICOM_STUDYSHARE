package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import java.util.Date


class MaterialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val uploadTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val uploadTypeTv: TextView = itemView.findViewById(R.id.uploadTypeTv)
    private val uploadDateTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val uploadAuthorTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val colorMaterialFrame: FrameLayout = itemView.findViewById(R.id.colorMaterialFrame)

    fun bindData(material: Material) {
        uploadTitleTv.text = material.materialName
        uploadTypeTv.text = material.materialType.toString()
        uploadDateTv.text = getTimeAgo(material.createdAt)
        uploadAuthorTv.text = "by ${material.materialAuthor}"
        colorMaterialFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(material.colorIcon))

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, MaterialDetailsActivity::class.java)
            intent.putExtra(IntentKeys.MATERIAL_NAME.name, material.materialName)
            intent.putExtra(IntentKeys.MATERIAL_TYPE.name, material.materialType.toString())
            intent.putExtra(IntentKeys.MATERIAL_TOPIC.name, material.materialTopic)
            intent.putExtra(IntentKeys.MATERIAL_AUTHOR.name, material.materialAuthor)
            intent.putExtra(IntentKeys.MATERIAL_DATE.name, material.createdAt.toString())
            intent.putExtra(IntentKeys.COLOR_ICON.name, material.colorIcon)
            intent.putExtra(IntentKeys.FILE_URL.name, material.fileUrl)
            intent.putExtra(IntentKeys.FILE_NAME.name, material.fileName)
            itemView.context.startActivity(intent)
        }

    }


}

fun getTimeAgo(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> android.text.format.DateFormat.format("MMM dd, yyyy", date).toString()
    }
}
