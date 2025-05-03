package course.controller;

import course.model.UserEntity;
import course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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
}