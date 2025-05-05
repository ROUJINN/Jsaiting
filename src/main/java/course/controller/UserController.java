package course.controller;

import course.model.DownloadRecord;
import course.model.FileEntity;
import course.model.UserEntity;
import course.service.UserService;
import course.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        UserEntity user = userService.authenticateUser(username, password);
        if (user != null) {
            session.setAttribute("currentUser", user);
            return "redirect:/"; // Redirect to index.html
        } else {
            model.addAttribute("error", "账号或密码错误");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        UserEntity user = userService.registerUser(username, password);
        if (user != null) {
            return "redirect:/login"; // Redirect to login page
        } else {
            model.addAttribute("error", "用户名已被注册");
            return "register";
        }
    }


    static public class DownloadRecordDTO {
        private FileEntity file;
        private DownloadRecord record;

        // 构造函数
        public DownloadRecordDTO(FileEntity file, DownloadRecord record) {
            this.file = file;
            this.record = record;
        }

        // Getter 和 Setter
        public FileEntity getFile() {
            return file;
        }

        public void setFile(FileEntity file) {
            this.file = file;
        }

        public DownloadRecord getRecord() {
            return record;
        }

        public void setRecord(DownloadRecord record) {
            this.record = record;
        }
    }

    public List<FileEntity> getFilesFromDownloadRecords(List<DownloadRecord> downloadRecords) {
        List<FileEntity> result = new ArrayList<>();
        Iterator<DownloadRecord> iterator = downloadRecords.iterator();

        while (iterator.hasNext()) {
            DownloadRecord dr = iterator.next();
            Optional<FileEntity> fileOpt = fileStorageService.getFileById(dr.getFileId());
            if (fileOpt.isPresent()) {
                result.add(fileOpt.get());
            } else {
                iterator.remove();  // 如果文件不存在，移除对应的下载记录
            }
        }

        return result;
    }


    @GetMapping("/user")
    public String userPage(HttpSession session, Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");
        if (currentUser != null) {
            //System.out.println("User: " + currentUser);

            List<FileEntity> uploadRecords = fileStorageService.SearchUploadFiles(currentUser.getUsername());
            //System.out.print(uploadRecords);
            System.out.println("uploadRecords:");
            for (FileEntity fe : uploadRecords) {
                System.out.println("fileName: " + fe.getFileName());
            }

            List<DownloadRecord> downloadRecords_ids = userService.getDownloadRecords(currentUser);
            List<FileEntity> downloadRecords = getFilesFromDownloadRecords(downloadRecords_ids);

            List<DownloadRecordDTO> downloadRecordDTOs = new ArrayList<>();
            for (int i = 0; i < downloadRecords_ids.size(); i++) {
                downloadRecordDTOs.add(new DownloadRecordDTO(downloadRecords.get(i), downloadRecords_ids.get(i)));
            }

            System.out.println("downloadRecords:");
            for (FileEntity fe : downloadRecords) {
                System.out.println("fileName: " + fe.getFileName());
            }
            model.addAttribute("username", currentUser.getUsername());
            model.addAttribute("uploadRecords", uploadRecords);
            model.addAttribute("downloadRecords", downloadRecordDTOs);

            return "user"; // 返回用户页面的视图名称
        } else {
            return "redirect:/login"; // 如果用户未登录，重定向到登录页面
        }
    }

}