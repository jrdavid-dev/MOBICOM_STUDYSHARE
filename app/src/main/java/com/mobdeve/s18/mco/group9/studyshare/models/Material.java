package com.mobdeve.s18.mco.group9.studyshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Material {

    // Enum for material types
    public enum MaterialType {
        Handouts,
        Notes,
        Reviewers
    }

    private long id;
    private String materialName;
    private String materialTopic;
    private String materialDescription;
    private String materialAuthor;
    private String colorIcon;
    private MaterialType materialType;
    private String courseId;
    private String fileUrl; // ✅ Cloud Storage download URL
    private String fileName; // ✅ Original file name (optional)
    private long fileSize; // ✅ File size in bytes (optional)
    private @ServerTimestamp Date createdAt;
    private @ServerTimestamp Date updatedAt;

    public Material() {
    }

    public Material(String materialName, String materialTopic, String materialDescription, String materialAuthor,
                    String colorIcon, long id, MaterialType materialType, String courseId,
                    String fileUrl, String fileName, long fileSize,
                    Date createdAt, Date updatedAt) {
        this.materialName = materialName;
        this.materialTopic = materialTopic;
        this.materialDescription = materialDescription;
        this.materialAuthor = materialAuthor;
        this.colorIcon = colorIcon;
        this.id = id;
        this.materialType = materialType;
        this.courseId = courseId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getMaterialTopic() { return materialTopic; }
    public void setMaterialTopic(String materialTopic) { this.materialTopic = materialTopic; }

    public String getMaterialDescription() { return materialDescription; }
    public void setMaterialDescription(String materialDescription) { this.materialDescription = materialDescription; }

    public String getMaterialAuthor() { return materialAuthor; }

    public void setMaterialAuthor(String materialAuthor) { this.materialAuthor = materialAuthor; }

    public String getColorIcon() { return colorIcon; }
    public void setColorIcon(String colorIcon) { this.colorIcon = colorIcon; }

    public MaterialType getMaterialType() { return materialType; }
    public void setMaterialType(MaterialType materialType) { this.materialType = materialType; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}