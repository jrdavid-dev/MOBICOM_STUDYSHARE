package com.mobdeve.s18.mco.group9.studyshare

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.mco.group9.studyshare.models.User
import java.util.Date


class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                val username = "${firstName.lowercase()}_${lastName.lowercase()}"

                val userData = hashMapOf(
                    MyFirestoreReferences.USERNAME_FIELD to username,
                    MyFirestoreReferences.FIRSTNAME_FIELD to firstName,
                    MyFirestoreReferences.LASTNAME_FIELD to lastName,
                    MyFirestoreReferences.EMAIL_FIELD to email,
                    MyFirestoreReferences.ID_FIELD to uid,
                    MyFirestoreReferences.CREATED_AT_FIELD to Date()
                )

                db.collection(MyFirestoreReferences.USERS_COLLECTION)
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, User?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)
                        onResult(true, user, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, null, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }
}
