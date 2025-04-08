package com.example.rolebasedauth.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        if (!model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", "An error occurred while loading the page.");
        }
        return "error";
    }
}
