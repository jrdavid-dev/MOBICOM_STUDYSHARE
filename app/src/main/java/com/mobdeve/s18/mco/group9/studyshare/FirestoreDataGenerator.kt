package com.mobdeve.s18.mco.group9.studyshare

import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s18.mco.group9.studyshare.models.User
import com.mobdeve.s18.mco.group9.studyshare.models.Course
import com.mobdeve.s18.mco.group9.studyshare.models.Material
import com.mobdeve.s18.mco.group9.studyshare.models.Subscription
import com.mobdeve.s18.mco.group9.studyshare.models.Material.MaterialType
import java.util.*

class FirestoreDataGenerator(private val db: FirebaseFirestore) {

    fun generateMockData() {
        generateUser()
        generateCourse()
        generateMaterials()
        generateSubscription()
    }

    private fun generateUser() {
        val user = User(
            "john_doe",
            "John",
            "Doe",
            "john.doe@example.com",
            "1001",  // Changed from 1001L to "1001"
            Date()
        )

        db.collection("users")
            .document(user.id)  // Removed .toString()
            .set(user)
            .addOnSuccessListener {
                println("✓ User created successfully")
            }
            .addOnFailureListener { e ->
                println("✗ Error creating user: ${e.message}")
            }
    }

    private fun generateSubscription() {
        val subscription = Subscription(
            "2001",
            "1001",
            "4001",  // Changed from 4001L to "4001"
            Date()
        )

        db.collection("subscriptions")
            .document(subscription.id)  // Removed .toString()
            .set(subscription)
            .addOnSuccessListener {
                println("✓ Subscription created successfully")
            }
            .addOnFailureListener { e ->
                println("✗ Error creating subscription: ${e.message}")
            }
    }

    private fun generateCourse() {
        val course = Course(
            "Introduction to Mobile Development",
            "1001",
            "Learn the fundamentals of mobile app development with Kotlin and Android",
            "#475569",
            "2001",  // Changed from 2001L to "2001"
            5,
            Date(),
            Date()
        )

        db.collection("courses")
            .document(course.id)  // Removed .toString()
            .set(course)
            .addOnSuccessListener {
                println("✓ Course created successfully")
            }
            .addOnFailureListener { e ->
                println("✗ Error creating course: ${e.message}")
            }
    }

    private fun generateMaterials() {
        val materials = listOf(
            Material(
                "Course Introduction Handouts",
                "Getting Started",
                "Overview of the course structure and objectives",
                "John Doe",
                "#475569",
                "3001",  // Changed from 3001L to "3001"
                MaterialType.Handouts,
                "2002",
                "https://example.com/docs/intro-handouts.pdf",
                "intro-handouts.pdf",
                15728640L,
                Date(),
                Date()
            ),
            Material(
                "Kotlin Basics Notes",
                "Programming Fundamentals",
                "Complete notes on Kotlin syntax and basic concepts",
                "John Doe",
                "#84CC16",
                "3002",  // Changed from 3002L to "3002"
                MaterialType.Notes,
                "2001",
                "https://example.com/docs/kotlin-notes.pdf",
                "kotlin-notes.pdf",
                2097152L,
                Date(),
                Date()
            ),
            Material(
                "Android UI Reviewer",
                "User Interface Design",
                "Study guide for Views, Layouts, and Material Design components",
                "John Doe",
                "#8B5CF6",
                "3003",  // Changed from 3003L to "3003"
                MaterialType.Reviewers,
                "2001",
                "https://example.com/docs/ui-reviewer.pdf",
                "ui-reviewer.pdf",
                5242880L,
                Date(),
                Date()
            ),
            Material(
                "First App Project Handouts",
                "Practical Project",
                "Step-by-step handouts for creating a simple Android application",
                "John Doe",
                "#0891B2",
                "3004",  // Changed from 3004L to "3004"
                MaterialType.Handouts,
                "2001",
                "https://example.com/docs/first-app-handouts.pdf",
                "first-app-handouts.pdf",
                1048576L,
                Date(),
                Date()
            ),
            Material(
                "Final Exam Reviewer",
                "Next Steps",
                "Comprehensive reviewer covering MVVM, Jetpack Compose, and modern Android development",
                "John Doe",
                "#475569",
                "3005",  // Changed from 3005L to "3005"
                MaterialType.Reviewers,
                "2001",
                "https://example.com/docs/final-reviewer.pdf",
                "final-reviewer.pdf",
                20971520L,
                Date(),
                Date()
            )
        )

        materials.forEach { material ->
            db.collection("materials")
                .document(material.id)  // Removed .toString()
                .set(material)
                .addOnSuccessListener {
                    println("✓ Material '${material.materialName}' created successfully")
                }
                .addOnFailureListener { e ->
                    println("✗ Error creating material: ${e.message}")
                }
        }
    }
}

// Usage:
// val db = FirebaseFirestore.getInstance()
// val generator = FirestoreDataGenerator(db)
// generator.generateMockData()