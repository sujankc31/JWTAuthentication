package com.example.rolebasedauth.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import com.example.rolebasedauth.Repository.UserRepository;

import com.example.rolebasedauth.Service.UserService;

@Controller
@RestController
@RequestMapping("/")
public class TestController {

    private final UserService userService;
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(TestController.class.getName());
    public TestController(UserRepository userRepository, UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/active-user-test")
    public String getActiveUsers(Model model) {
        Integer activeUserCount = userService.getTotalActiveUsers();
        log.info("Total active users: ========== " + activeUserCount);
        model.addAttribute("activeUserCount", activeUserCount);
        return "/admin/dashboard"; // Replace with your Thymeleaf template name
    }

    @GetMapping("/total-user-test")
    public String getTotalUsers(Model model) {
        Integer totalUserCount = userService.getTotalUsers();
        log.info("Total Total users: ========== " + totalUserCount);
        model.addAttribute("TotalUserCount", totalUserCount);
        return "/admin/dashboard1"; // Replace with your Thymeleaf template name
    }

    //  @GetMapping("/userss-stats")
    // public Map<String, Integer> getUserStats() {
    //     Map<String, Integer> stats = new HashMap<>();
    //     stats.put("totalUsers", userService.getTotalUsers());
    //     stats.put("activeUsers", userService.getTotalActiveUsers());
    //     stats.put("inactiveUsers", userService.getTotalInactiveUsers());
    //     return stats;
    // }


    
    
}
