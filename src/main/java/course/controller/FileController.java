package course.controller;

import course.model.FileEntity;
import course.model.UserEntity;
import course.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/")
    public String homepage(HttpSession session, Model model, @RequestParam(required = false) String keyword) {
        if(session.getAttribute("currentUser") == null){
            return "login";
        }
        List<FileEntity> files;
        if (keyword != null && !keyword.isEmpty()) {
            files = fileStorageService.searchFiles(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            files = fileStorageService.getAllFiles();
        }
        model.addAttribute("files", files);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("description") String description,
                             @RequestParam("courseName") String courseName,
                             @RequestParam(value = "courseShortName", required = false) String courseShortName,
                             @RequestParam("instructor") String instructor,
                             @RequestParam("semester") String semester,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择要上传的文件！");
            return "redirect:/";
        }

        try {
            // Get the current logged-in user
            UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");
            String username = currentUser != null ? currentUser.getUsername() : "未知用户";
            
            // Handle null values for optional fields
            if (courseShortName == null) courseShortName = "";
            
            FileEntity savedFile = fileStorageService.storeFile(
                file, 
                description, 
                courseName, 
                courseShortName, 
                instructor, 
                semester, 
                username
            );
            
            redirectAttributes.addFlashAttribute("message", 
                "文件 '" + savedFile.getFileName() + "' 上传成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "文件上传失败: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest request) {
        Optional<FileEntity> fileEntityOpt = fileStorageService.getFileById(id);
        
        if (!fileEntityOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        FileEntity fileEntity = fileEntityOpt.get();
        Resource resource = fileStorageService.loadFileAsResource(fileEntity.getStoredFilePath());

        // 检测文件的内容类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        // 如果类型未检测到，则默认使用通用类型
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/search")
    public String searchFiles(@RequestParam String keyword, Model model) {
        List<FileEntity> files = fileStorageService.searchFiles(keyword);
        model.addAttribute("files", files);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}