package com.mobdeve.s18.mco.group9.studyshare.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Course {

    private long id;
    private String courseName;
    private String courseAuthorId;
    private String courseDetails;
    private String colorIcon;
    private int materialCount;
    private @ServerTimestamp Date createdAt;
    private @ServerTimestamp Date updatedAt;

    public Course(){

    }

    public Course(String courseName, String courseAuthorId, String courseDetails, String colorIcon, long id,
                  int materialCount, Date createdAt, Date updatedAt){

        this.courseName = courseName;
        this.courseAuthorId = courseAuthorId;
        this.courseDetails = courseDetails;
        this.colorIcon = colorIcon;
        this.id = id;
        this.materialCount = materialCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseAuthor() { return courseAuthorId; }
    public void setCourseAuthor(String courseAuthor) { this.courseAuthorId = courseAuthor; }

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
