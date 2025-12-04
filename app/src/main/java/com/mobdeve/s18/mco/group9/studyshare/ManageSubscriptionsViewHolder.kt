package com.mobdeve.s18.mco.group9.studyshare

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.models.Course


class ManageSubscriptionsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val courseTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val courseMaterialCountTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val courseDetailsAuthorTv: TextView = itemView.findViewById(R.id.profileAuthorTv)
    private val subscribeBtn: TextView = itemView.findViewById(R.id.subscribeBtn)
    private val colorManageSubsFrame: FrameLayout = itemView.findViewById(R.id.colorManageSubsFrame)
    private val current_user_id = "1001"

    fun bindData(course: Course, isSubscribed: Boolean) {

        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
        courseDetailsAuthorTv.text = course.courseAuthor
        courseMaterialCountTv.text = "${course.materialCount} Materials"
        colorManageSubsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(course.colorIcon))

        if (isSubscribed) {
            subscribeBtn.text = "Unsubscribe"
            subscribeBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0891B2"))
            subscribeBtn.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            subscribeBtn.text = "Subscribe"
            subscribeBtn.backgroundTintList = null
            subscribeBtn.setTextColor(Color.parseColor("#000000"))
        }

        colorManageSubsFrame.setOnClickListener {
            val intent = Intent(itemView.context, CourseDetailsActivity::class.java)
            intent.putExtra(IntentKeys.COURSE_ID.name, course.id.toString())
            intent.putExtra(IntentKeys.COURSE_NAME.name, course.courseName)
            intent.putExtra(IntentKeys.MATERIAL_COUNT.name, course.materialCount.toString())
            intent.putExtra(IntentKeys.LAST_UPDATED.name, getTimeAgo(course.updatedAt))
            intent.putExtra(IntentKeys.COLOR_ICON.name, course.colorIcon)
            itemView.context.startActivity(intent)
        }

        subscribeBtn.setOnClickListener{
            if(subscribeBtn.text == "Subscribe"){
                //SUBSCRIBE
                subscribeBtn.text = "Unsubscribe"
                subscribeBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0891B2"))
                subscribeBtn.setTextColor(Color.parseColor("#FFFFFF"))

                val db  = Firebase.firestore
                // TODO CHANGE THE HARDCODED USERID
                val subsRef = db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION).document()
                val subId = subsRef.id
                val subData = hashMapOf(
                    MyFirestoreReferences.ID_FIELD to subId,
                    MyFirestoreReferences.USER_ID_FIELD to current_user_id,
                    MyFirestoreReferences.COURSE_ID_FIELD to course.id,
                    MyFirestoreReferences.CREATED_AT_FIELD to FieldValue.serverTimestamp()
                )
                subsRef.set(subData)
                    .addOnSuccessListener {  }
                    .addOnFailureListener { exception ->
                        Log.w("CHANGE_ME", "Error getting courses", exception)
                    }
            } else {
                //UNSUBSCRIBE
                subscribeBtn.text = "Subscribe"
                subscribeBtn.backgroundTintList = null
                subscribeBtn.setTextColor(Color.parseColor("#000000"))

                val db = Firebase.firestore
                val subsRef = db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)

                subsRef.whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
                    .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, course.id)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {

                        } else {
                            for(document in documents){
                                document.reference.delete()
                                    .addOnSuccessListener {  }
                                    .addOnFailureListener { exception ->
                                        Log.w("CHANGE_ME", "Error getting courses", exception)
                                    }
                            }

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("CHANGE_ME", "Error getting courses", exception)
                    }
            }
        }
    }

}

