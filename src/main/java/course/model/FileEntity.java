package course.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "files")
public class FileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fileName;
    
    private String fileType;
    
    // Merged field for description and additionalNotes
    private String description;
    
    private String storedFilePath;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    
    private long size;
    
    // Course information fields
    private String courseName;     // 课程全称
    private String courseShortName; // 课程简称
    private String instructor;     // 授课老师
    private String semester;       // 学期
    
    // Field to store the uploader's username
    private String uploaderUsername;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "fileEntity")
    private List<Comment> comments = new ArrayList<>();


    public FileEntity() {
    }
    
    public FileEntity(String fileName, String fileType, String description, String storedFilePath, long size,
                     String courseName, String courseShortName, String instructor, String semester, 
                     String uploaderUsername) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.description = description;
        this.storedFilePath = storedFilePath;
        this.uploadDate = new Date();
        this.size = size;
        this.courseName = courseName;
        this.courseShortName = courseShortName;
        this.instructor = instructor;
        this.semester = semester;
        this.uploaderUsername = uploaderUsername;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoredFilePath() {
        return storedFilePath;
    }

    public void setStoredFilePath(String storedFilePath) {
        this.storedFilePath = storedFilePath;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    // Getters and setters for course information
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseShortName() {
        return courseShortName;
    }

    public void setCourseShortName(String courseShortName) {
        this.courseShortName = courseShortName;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setFileEntity(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setFileEntity(null);
    }

    @Entity
    @Table(name = "comments")
    public static class Comment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String commenterUsername;

        private String text;

        @Temporal(TemporalType.TIMESTAMP)
        private Date commentDate;

        @ManyToOne
        @JoinColumn(name = "file_id", nullable = false)
        private FileEntity fileEntity;

        public Comment() {
        }

        public Comment(String commenterUsername, String text) {
            this.commenterUsername = commenterUsername;
            this.text = text;
            this.commentDate = new Date();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCommenterUsername() {
            return commenterUsername;
        }

        public void setCommenterUsername(String commenterUsername) {
            this.commenterUsername = commenterUsername;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Date getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(Date commentDate) {
            this.commentDate = commentDate;
        }

        public FileEntity getFileEntity() {
            return fileEntity;
        }

        public void setFileEntity(FileEntity fileEntity) {
            this.fileEntity = fileEntity;
        }
    }


}