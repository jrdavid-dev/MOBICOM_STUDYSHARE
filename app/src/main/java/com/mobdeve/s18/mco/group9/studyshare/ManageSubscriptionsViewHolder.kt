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
import com.google.firebase.auth.FirebaseAuth


class ManageSubscriptionsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val courseTitleTv: TextView = itemView.findViewById(R.id.profileCourseNameTv)
    private val courseMaterialCountTv: TextView = itemView.findViewById(R.id.profileMaterialCountTv)
    private val courseDetailsTv: TextView = itemView.findViewById(R.id.profileCourseDetailsTv)
    private val courseDetailsAuthorTv: TextView = itemView.findViewById(R.id.profileAuthorTv)
    private val subscribeBtn: TextView = itemView.findViewById(R.id.subscribeBtn)
    private val colorManageSubsFrame: FrameLayout = itemView.findViewById(R.id.colorManageSubsFrame)
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    private val TAG = "MANAGE_SUBSCRIPTIONS_VIEWHOLDER"

    private var current_user_name: String = "Unknown User"

    fun bindData(course: Course, isSubscribed: Boolean) {

        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
            .document(current_user_id.toString())
            .get()
            .addOnSuccessListener { userDocument ->
                val firstName = userDocument.getString(MyFirestoreReferences.FIRST_NAME_FIELD) ?: ""
                val lastName = userDocument.getString(MyFirestoreReferences.LAST_NAME_FIELD) ?: ""
                current_user_name = "$firstName $lastName".trim()
                Log.d(TAG, "Fetched user name: $current_user_name")
                courseDetailsAuthorTv.text = current_user_name

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching user", exception)
                courseDetailsAuthorTv.text = current_user_name
            }

        courseTitleTv.text = course.courseName
        courseDetailsTv.text = course.courseDetails
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

                current_user_id ?: return@setOnClickListener
                val db  = Firebase.firestore
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
                        Log.w(TAG, "Error getting subscriptions", exception)
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
                                        Log.w(TAG, "Error getting subscriptions", exception)
                                    }
                            }

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting subscriptions", exception)
                    }
            }
        }
    }

}

