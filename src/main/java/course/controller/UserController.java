package course.controller;

import course.model.DownloadRecord;
import course.model.UploadRecord;
import course.model.UserEntity;
import course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

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

    @GetMapping("/user")
    public String userPage(HttpSession session, Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("currentUser");
        if (currentUser != null) {
            // 假设 UserService 中有方法来获取上传和下载记录
            List<UploadRecord> uploadRecords = userService.getUploadRecords(currentUser);
            List<DownloadRecord> downloadRecords = userService.getDownloadRecords(currentUser);

            model.addAttribute("username", currentUser.getUsername());
            model.addAttribute("uploadRecords", uploadRecords);
            model.addAttribute("downloadRecords", downloadRecords);

            return "user"; // 返回用户页面的视图名称
        } else {
            return "redirect:/login"; // 如果用户未登录，重定向到登录页面
        }
    }

}