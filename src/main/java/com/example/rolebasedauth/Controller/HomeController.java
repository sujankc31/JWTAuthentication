package com.example.rolebasedauth.Controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Dto.EmailDto;
import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Entity.Email;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Entity.Role.ERole;
import com.example.rolebasedauth.Entity.User;
import com.example.rolebasedauth.Service.UserService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.example.rolebasedauth.Service.EmailService;
import com.example.rolebasedauth.Service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public HomeController(UserService userService, RoleService roleService, EmailService emailService,
            BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.emailService = emailService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/homepage";
    }

    @GetMapping("/custom-login")
    public String login() {
        return "custom-login";
    }

    @GetMapping("/page-not-found")
    public String homepage() {
        return "404";
    }

    @PostMapping("/login1")
    public String loginUser(@RequestParam(name = "username", required = true) String username,
            @RequestParam("password") String password,
            @RequestParam String captcha,
            HttpSession session,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            String sessionCaptcha = (String) session.getAttribute("captcha");
            if (sessionCaptcha == null || !captcha.equalsIgnoreCase(sessionCaptcha)) {
                throw new Exception("Captcha didn't match.");
            }
            session.removeAttribute("captcha"); // clear after use
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("captchaErrorMessage", e.getMessage());
            return "redirect:/custom-login"; // return to login page
        }
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElse(null);
        // Check if user exists
        // Combined error check for both username and password
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "The username or password you have enetered is incorrect. Please try again.");
            return "redirect:/custom-login?error=true";
        }
        // Get the user's roles from the database
        Set<ERole> userRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        // Get the allowed roles from the database
        Set<ERole> allowedRoles = Set.of(ERole.ROLE_USER, ERole.ROLE_ADMIN, ERole.ROLE_MODERATOR);
        // Check if the user's roles are present in the allowed roles
        if (!allowedRoles.containsAll(userRoles)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You do not have the required roles to access this system");
            model.addAttribute("error", "You do not have the required roles to access this system");
            return "custom-login?error=true";
        }
        // Check if the user is active
        if (!user.getIsActive()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Your account is not active. Please contact the administrator.");
            return "redirect:/custom-login?error=true";
        }

        // Authenticate the user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        Authentication auth = authenticationManager.authenticate(authenticationToken);

        // If authentication is successful, set the authentication in the
        // SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);
        // Update last login
        userService.updateLastLogin(username);
        // If login success
        redirectAttributes.addFlashAttribute("successMessage", "Login Successful");
        redirectAttributes.addFlashAttribute("ErrorMessage", "Login Unsuccessful");
        redirectAttributes.addFlashAttribute("user", user); // Pass user info if needed
        model.addAttribute("message", "Login Successful");
        model.addAttribute("user", user); // Pass user info if needed

        // Redirect based on user role
        if (userRoles != null && userRoles.contains(ERole.ROLE_ADMIN)) {
            return "redirect:/admin/dashboard";
        } else if (userRoles != null && userRoles.contains(ERole.ROLE_MODERATOR)) {
            return "redirect:/dashboard";
        } else {
            return "redirect:/dashboard";
        }
        // Redirect after successful login
    }

    @GetMapping("admin/usermanagement")
    public String userList(Model model, Authentication authentication) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("allRoles", roles);
        List<UserDto> users = userService.getAllUsers();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("users", users);
        model.addAttribute("roles", authentication.getAuthorities());
        return "admin/userList";
    }

    @GetMapping("admin/rolemanagement")
    public String roleList(Model model, Authentication authentication) {
        List<Role> allroles = roleService.getAllRoles();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("allRoles", allroles);
        model.addAttribute("roles", authentication.getAuthorities());
        return "admin/roleList";
    }

    // @GetMapping("admin/emails")
    // public String emailsList(Model model, Authentication authentication) {
    // List<Email> emails = emailService.getAllEmails();
    // model.addAttribute("username", authentication.getName());
    // model.addAttribute("emails", emails);
    // model.addAttribute("roles", authentication.getAuthorities());
    // return "admin/emailsList";
    // }

    @GetMapping("admin/emails")
    public String emailsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {

        Page<Email> emailPage = emailService.getEmailsWithPagination(page, size);
            
        System.out.println(emailPage.getContent());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("emails", emailPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", emailPage.getTotalPages());
        model.addAttribute("totalItems", emailPage.getTotalElements());

        return "admin/emailsList";
    }

    // In your controller method that handles the register page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "Register - PortFolio App");
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserCreateDto userCreateDto, RedirectAttributes redirectAttributes) {
        if (userService.createUser(userCreateDto) != null) {
            redirectAttributes.addFlashAttribute("successMessage1",
                    "Registration successful, please login to access the system");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage1", "Registration unsuccessful, please try again");
        }
        return "redirect:/custom-login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        // Fetch user details from service by username
        UserDto user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);

        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        // Fetch user details from service by username
        UserDto user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/user-stats")
    public Map<String, Integer> getUserStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("activeUsers", userService.getTotalActiveUsers());
        stats.put("inactiveUsers", userService.getTotalInactiveUsers());
        return stats;
    }

    @GetMapping("/admin/dashboard")
    public String admin_dashboard( Authentication authentication,Model model) {
        
        List<UserDto> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        // Add active user count to model
        addInactiveUserCountToModel(model);
        addTotalUserCountToModel(model);
        addActiveUserCountToModel(model);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        return "admin/dashboard";
    }

    // Method to add active user count to model
    private void addActiveUserCountToModel(Model model) {
        Integer activeUserCount = userService.getTotalActiveUsers();
        model.addAttribute("activeUserCount", activeUserCount);
    }

    private void addInactiveUserCountToModel(Model model) {
        Integer inactiveUserCount = userService.getTotalInactiveUsers();
        model.addAttribute("inactiveUserCount", inactiveUserCount);
    }

    private void addTotalUserCountToModel(Model model) {
        Integer totalUserCount = userService.getTotalUsers();
        model.addAttribute("totalUserCount", totalUserCount);
    }

    @GetMapping("/rolemanagement")
    public String RoleManagement(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());
        return "admin/roleList";
    }

    @PostMapping("/aadmin/add-role")
    public String createRole(@ModelAttribute Role role, RedirectAttributes redirectAttributes) {
        try {
            roleService.createRole(role);
            redirectAttributes.addFlashAttribute("successMessage", "Role added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding role: " + e.getMessage());
        }
        return "redirect:/admin/rolemanagement";
    }

    @GetMapping("/homepage")
    public String index(Model model) throws IOException, ServletException {
        try {
            model.addAttribute("pageTitle", "Homepage");
            return "homepage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid input provided: " + e.getMessage());
            return "error";
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", "Security error: " + e.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            if (e.getMessage() == null) {
                model.addAttribute("errorMessage", "An unexpected error occurred");
            }
            return "error";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
        return "error";
    }

    @GetMapping("/total-users")
    public ResponseEntity<Integer> getTotalUsers() {
        Integer count = userService.getTotalUsers();
        System.out.println("===============");
        System.out.println("Total users: " + count);
        System.out.println("===============");
        return ResponseEntity.ok(count);
    }

    // Edit profile of user by themself
    @PostMapping("/edit-profile")
    public String editProfile(@AuthenticationPrincipal User user, Model model,
            @RequestParam(name = "email", required = false) String email, RedirectAttributes redirectAttributes) {
        try {
            userService.editProfile(user, email);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    // @GetMapping("/total-active-users")
    // public ResponseEntity<Long> getTotalActiveUsers() {
    // Long count = userService.getTotalActiveUsers();
    // System.out.println("===============" );
    // System.out.println("Total Active users: " + count);
    // System.out.println("===============" );
    // return ResponseEntity.ok(count);
    // }

    @PostMapping("admin/edit-user")
    public ResponseEntity<UserDto> editUser(@ModelAttribute UserDto userDto) {
        try {
            UserDto user = userService.editUser(userDto);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prevent caching
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        // Generate captcha text and store in session
        String capText = captchaProducer.createText();
        request.getSession().setAttribute("captcha", capText);

        // Create captcha image and write to response
        BufferedImage bi = captchaProducer.createImage(capText);
        ImageIO.write(bi, "jpg", response.getOutputStream());

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @PostMapping("/verify-captcha")
    public String verify(@RequestParam("captcha") String userCaptcha, HttpSession session) {
        String captcha = (String) session.getAttribute("captcha");
        if (captcha != null && captcha.equalsIgnoreCase(userCaptcha)) {
            return "Captcha verified!";
        }
        return "Invalid captcha.";
    }

    @PostMapping("admin/send-message")
    public String sendMessage(@Valid EmailDto emailDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (result.hasErrors()) {
            // Return to the form page with error messages
            return "homepage"; // Assuming your form is in a template named "contact"
        }

        try {
            // Save the email to the database
            emailService.saveEmail(emailDto);

            // Add success message to be displayed after redirect
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your message has been sent successfully!");

            // Redirect to prevent form resubmission
            return "redirect:/homepage"; // Change this to your desired redirect URL

        } catch (Exception e) {
            // Handle errors
            redirectAttributes.addFlashAttribute("errorMessage",
                    "There was an error sending your message. Please try again.");

            return "redirect:/homepage"; // Change this to your desired redirect URL
        }
    }

}
