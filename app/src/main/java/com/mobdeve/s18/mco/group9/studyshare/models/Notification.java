package com.mobdeve.s18.mco.group9.studyshare.models;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notification {

    // Enum for notification types
    public enum NotificationType {
        MATERIAL_UPLOAD,
        MATERIAL_EDIT,
        MATERIAL_DELETE,
        COURSE_EDIT,
        COURSE_DELETE
    }

    private String id;
    private String userId;              // Who receives this notification
    private String courseId;            // Which course it's about
    private NotificationType type;      // Type of notification

    // Optional fields depending on type
    private String materialId;          // If it's material-related
    private String materialName;        // For display
    private String authorName;           // Who did the action (uploader/editor name)
    private boolean isRead;             // Track if user has seen it
    private @ServerTimestamp Date createdAt;


    public Notification() {}
    public Notification(String id, String userId, NotificationType type,
                        String materialId, String materialName, String authorName,
                        Boolean isRead, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.materialId = materialId;
        this.materialName = materialName;
        this.authorName = authorName;
        this.isRead = isRead;
        this.createdAt = createdAt;

    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }


    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String actorName) { this.authorName = actorName; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}