package course.service;

import course.config.FileStorageProperties;
import course.model.FileEntity;
import course.repository.DownloadRecordRepository;
import course.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    private final FileRepository fileRepository;




    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileEntity storeFile(MultipartFile file, String description, String courseName, 
                                String courseShortName, String instructor, String semester, 
                                String uploaderUsername) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // Generate unique filename to prevent overwrite
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            
            // Copy file to the target location (Replacing existing file with the same name)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save file info to database with the merged description field
            FileEntity fileEntity = new FileEntity(
                originalFileName,
                file.getContentType(),
                description,
                uniqueFileName,
                file.getSize(),
                courseName,
                courseShortName,
                instructor,
                semester,
                uploaderUsername
            );
            
            return fileRepository.save(fileEntity);
            
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    // For backward compatibility with existing code
    public FileEntity storeFile(MultipartFile file, String description) {
        return storeFile(file, description, "未指定", "", "未指定", "未指定", "未知用户");
    }

    public Resource loadFileAsResource(String storedFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + storedFileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + storedFileName, ex);
        }
    }
    
    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }
    
    public List<FileEntity> searchFiles(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return fileRepository.findAll();
        }
        return fileRepository.searchByKeyword(keyword);
    }
    public List<FileEntity> SearchUploadFiles(String username) {
        return fileRepository.findByUploaderUsername(username);
    }

    public Optional<FileEntity> getFileById(Long id) {
        return fileRepository.findById(id);
    }

    public List<FileEntity> searchFilesByIds(List<Long> ids) {
        return fileRepository.findByIdIn(ids);
    }

    public boolean deleteFileById(Long id, String username) {
        Optional<FileEntity> fileOpt = fileRepository.findById(id);
        if (fileOpt.isPresent()) {
            FileEntity file = fileOpt.get();
            if (!file.getUploaderUsername().equals(username)) {
                return false; // 不允许删除别人上传的文件
            }
            // 删除数据库记录和物理文件
            fileRepository.delete(file);
            try {
                Files.deleteIfExists(Paths.get(file.getStoredFilePath()));
            } catch (IOException e) {
                // 记录日志或抛出
            }
            return true;
        }
        return false;
    }


}