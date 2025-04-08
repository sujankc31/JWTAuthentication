package com.example.rolebasedauth.Controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.rolebasedauth.Repository.UserRepository;
import com.example.rolebasedauth.Dto.UserCreateDto;
import com.example.rolebasedauth.Dto.UserDto;
import com.example.rolebasedauth.Entity.Role;
import com.example.rolebasedauth.Entity.Role.ERole;
import com.example.rolebasedauth.Entity.User;
import com.example.rolebasedauth.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;


    public HomeController(UserService userService, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
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

    @PostMapping("/login1")
    public String loginUser(@RequestParam(name="username", required= true) String username,
            @RequestParam("password") String password,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElse(null);
        // Check if user exists
        // Combined error check for both username and password
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "The username or password you have enetered is incorrect. Please try again.");
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
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have the required roles to access this system");
            model.addAttribute("error", "You do not have the required roles to access this system");
            return "custom-login?error=true";
        }

        // Authenticate the user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        Authentication auth = authenticationManager.authenticate(authenticationToken);

        // If authentication is successful, set the authentication in the
        // SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);

        // If login success
        redirectAttributes.addFlashAttribute("successMessage", "Login Successful");
        redirectAttributes.addFlashAttribute("ErrorMessage","Login Unsuccessful");
        redirectAttributes.addFlashAttribute("user", user); // Pass user info if needed
        model.addAttribute("message", "Login Successful");
        model.addAttribute("user", user); // Pass user info if needed
        return "redirect:/dashboard"; // Redirect after successful login
    }

    // @GetMapping("/register")
    // public String register() {
    // return "register";
    // }

    // In your controller method that handles the register page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pageTitle", "Register - PortFolio App");
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserCreateDto userCreateDto) {
        userService.createUser(userCreateDto);
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
        return "dashboard";
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
}
