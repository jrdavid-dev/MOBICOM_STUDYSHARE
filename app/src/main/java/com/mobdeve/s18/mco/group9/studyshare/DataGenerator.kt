package com.mobdeve.s18.mco.group9.studyshare

class DataGenerator {
    companion object {
        private val course1 : Course = Course("Mobile Computing", 12)
        private val course2 : Course = Course("Computer Architecture", 14)

        private val course3 : Course = Course("Mobile Computing", 12)
        private val course4 : Course = Course("Computer Architecture", 14)

        private val course5 : Course = Course("Computer Architecture", 14)

        private val material1 = Material("SQL Joins and Queries", "Lecture Notes", "2 days ago", "Jose David")
        private val material2 = Material("Introduction to Data Structures", "Handouts", "5 days ago", "Hanielle Chua")
        private val material3 = Material("Android RecyclerView Tutorial", "Video Lecture", "1 week ago", "Gabrielle Kelsey")
        private val material4 = Material("Design Patterns in Kotlin", "Lecture Notes", "3 weeks ago", "Jose David")

        fun generateCourse(): ArrayList<Course> {
            return arrayListOf<Course>(course1, course2, course3, course4, course5)
        }

        fun generateUpload(): ArrayList<Material> {
            return arrayListOf<Material>(material1, material2, material3, material4)
        }
    }
}