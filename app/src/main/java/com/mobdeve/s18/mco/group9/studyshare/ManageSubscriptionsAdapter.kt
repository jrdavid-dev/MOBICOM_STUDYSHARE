package com.mobdeve.s18.mco.group9.studyshare

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue

import com.mobdeve.s18.mco.group9.studyshare.models.Course

class ManageSubscriptionsAdapter(private val subscribedCourses: ArrayList<Course>,
    private val remainingCourses: ArrayList<Course>): RecyclerView.Adapter<ManageSubscriptionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSubscriptionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.courses_layout, parent, false)
        return ManageSubscriptionsViewHolder(view) { course, isSubscribed, position ->
            handleSubscriptionToggle(course, isSubscribed, position)
        }
    }

    override fun onBindViewHolder(holder: ManageSubscriptionsViewHolder, position: Int) {
        if (position < subscribedCourses.size) {
            val course = subscribedCourses[position]
            holder.bindData(course, isSubscribed = true)
        } else {
            val course = remainingCourses[position - subscribedCourses.size]
            holder.bindData(course, isSubscribed = false)
        }
    }

    override fun getItemCount(): Int = subscribedCourses.size + remainingCourses.size

    private fun handleSubscriptionToggle(course: Course, wasSubscribed: Boolean, position: Int) {
        if (wasSubscribed) {

            subscribedCourses.remove(course)
            remainingCourses.add(0, course)
            unsubscribeToCourse(course)
        } else {
            remainingCourses.remove(course)
            subscribedCourses.add(course)
            subscribeToCourse(course)
        }

        notifyDataSetChanged()
    }

    private fun subscribeToCourse(course : Course){
        val current_user_id = "1001"
        val db = Firebase.firestore
        val subsRef = db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION).document()
        val subsId = subsRef.id
        val data = hashMapOf(
            "userId" to current_user_id,
            "courseId" to course.id.toString(),
            "createdAt" to FieldValue.serverTimestamp(),
            MyFirestoreReferences.ID_FIELD to subsId
        )

        subsRef.set(data)
            .addOnSuccessListener { documentReference ->
                Log.d("MANAGE_SUBSCRIPTIONS_ACTIVITY", "DocumentSnapshot written with ID: 4002")
            }
            .addOnFailureListener { e ->
                Log.w("MANAGE_SUBSCRIPTIONS_ACTIVITY", "Error adding document", e)
            }

    }

    private fun unsubscribeToCourse(course : Course){
        val current_user_id = "1001"
        val db = Firebase.firestore

        db.collection(MyFirestoreReferences.SUBSCRIPTIONS_COLLECTION)
            .whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id)
            .whereEqualTo(MyFirestoreReferences.COURSE_ID_FIELD, course.id.toString())
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("MANAGE_SUBSCRIPTIONS_ACTIVITY", "Successfully unsubscribed")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MANAGE_SUBSCRIPTIONS_ACTIVITY", "Failed to delete subscription", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MANAGE_SUBSCRIPTIONS_ACTIVITY", "Failed to delete subscription", e)
            }

    }
}
