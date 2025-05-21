package com.example.rolebasedauth.Controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        System.out.println("Captcha endpoint called");
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
}
