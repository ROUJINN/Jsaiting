package course.controller;

import course.model.DownloadRecord;
import course.model.FileEntity;
import course.model.FileEntity.Comment;
import course.model.UserEntity;
import course.repository.FileRepository;
import course.repository.DownloadRecordRepository;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private DownloadRecordRepository downloadRecordRepository;

    @Autowired
    private FileRepository fileRepository;

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
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest request, HttpSession session) {
        UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");


        Optional<FileEntity> fileEntityOpt = fileStorageService.getFileById(id);

        if (!fileEntityOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        FileEntity fileEntity = fileEntityOpt.get();
        Resource resource = fileStorageService.loadFileAsResource(fileEntity.getStoredFilePath());

        // 记录下载行为
        DownloadRecord record = new DownloadRecord();
        record.setFileId(fileEntity.getId());
        record.setFileName(fileEntity.getFileName());
        record.setDownloadTime(new Date());
        record.setUsername(currentUser != null ? currentUser.getUsername() : "匿名用户");
        downloadRecordRepository.save(record);

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

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录！");
            return "redirect:/";
        }

        try {
            boolean deleted = fileStorageService.deleteFileById(id, currentUser.getUsername());
            if (deleted) {
                redirectAttributes.addFlashAttribute("message", "文件删除成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "您无权删除该文件，或文件不存在。");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }

        return "redirect:/user"; // 假设这是用户页面
    }

    @PostMapping("/comments/{fileId}")
    public String addComment(@PathVariable Long fileId,
                             @RequestParam("commentText") String commentText,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录后再发表评论！");
            return "redirect:/login";
        }

        Optional<FileEntity> fileEntityOpt = fileRepository.findById(fileId);
        if (!fileEntityOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "文件未找到，无法发表评论！");
            return "redirect:/search";
        }

        FileEntity fileEntity = fileEntityOpt.get();
        Comment comment = new Comment(currentUser.getUsername(), commentText);
        fileEntity.addComment(comment);
        fileRepository.save(fileEntity);

        redirectAttributes.addFlashAttribute("message", "评论已成功发布！");
        return "redirect:/";
    }

    @GetMapping("/comments/{fileId}")
    @ResponseBody
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long fileId) {
        Optional<FileEntity> fileEntityOpt = fileRepository.findById(fileId);
        if (fileEntityOpt.isPresent()) {
            List<Comment> comments = fileEntityOpt.get().getComments();
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}