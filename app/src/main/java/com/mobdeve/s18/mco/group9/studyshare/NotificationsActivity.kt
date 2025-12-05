package com.mobdeve.s18.mco.group9.studyshare


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.mobdeve.s18.mco.group9.studyshare.databinding.NotificationsPageBinding
import com.mobdeve.s18.mco.group9.studyshare.models.Notification

class NotificationsActivity : AppCompatActivity() {


    private val auth by lazy { FirebaseAuth.getInstance() }
    private val current_user_id: String?
        get() = auth.currentUser?.uid

    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: NotificationsPageBinding = NotificationsPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.firestore

        val coursesRef = db.collection(MyFirestoreReferences.NOTIFICATIONS_COLLECTION)

        val notifQuery =
            coursesRef.whereEqualTo(MyFirestoreReferences.USER_ID_FIELD, current_user_id).orderBy(
                MyFirestoreReferences.CREATED_AT_FIELD,Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Notification>()
            .setQuery(notifQuery, Notification::class.java)
            .build()

        notificationsAdapter = NotificationsAdapter(options)

        viewBinding.notificationRecyclerView.itemAnimator = null


        viewBinding.notificationRecyclerView.adapter = notificationsAdapter
        viewBinding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)


    }

    override fun onStart() {
        super.onStart()
        this.notificationsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        this.notificationsAdapter.stopListening()
    }
}

