package com.mobdeve.s18.mco.group9.studyshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Subscription {

    private long id;
    private String userId;
    private String courseId;
    private @ServerTimestamp Date createdAt;

    public Subscription(){

    }

    public Subscription(String courseId, String userId, long id, Date createdAt){
        this.courseId = courseId;
        this.userId = userId;
        this.id = id;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
