package com.mobdeve.s18.mco.group9.studyshare.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Course {

    private String id;
    private String courseName;
    private String courseAuthor;
    private String courseDetails;
    private String colorIcon;
    private int materialCount;
    private @ServerTimestamp Date createdAt;
    private @ServerTimestamp Date updatedAt;

    public Course(){

    }

    public Course(String courseName, String courseAuthor, String courseDetails, String colorIcon, String id,
                  int materialCount, Date createdAt, Date updatedAt){

        this.courseName = courseName;
        this.courseAuthor = courseAuthor;
        this.courseDetails = courseDetails;
        this.colorIcon = colorIcon;
        this.id = id;
        this.materialCount = materialCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseAuthor() { return courseAuthor; }
    public void setCourseAuthor(String courseAuthor) { this.courseAuthor = courseAuthor; }

    public String getCourseDetails() { return courseDetails; }
    public void setCourseDetails(String courseDetails) { this.courseDetails = courseDetails; }
    public String getColorIcon() { return colorIcon; }
    public void setColorIcon(String colorIcon) { this.colorIcon = colorIcon; }
    public int getMaterialCount() { return materialCount; }
    public void setMaterialCount(int materialCount) { this.materialCount = materialCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }


}
