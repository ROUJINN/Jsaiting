package course.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class DownloadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fileId;

    private String username;

    private Date downloadTime;

    // 可选：记录文件名以备查询
    private String fileName;

    // Getter & Setter
    public Long getId() { return id; }
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Date getDownloadTime() { return downloadTime; }
    public void setDownloadTime(Date downloadTime) { this.downloadTime = downloadTime; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
