package com.example.Alisam_Codes.controllers;

import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.models.Order;
import com.example.Alisam_Codes.services.UserService;
import com.example.Alisam_Codes.repositories.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Fetch fresh user data just in case it was updated
        User freshUser = userService.getUserById(loggedInUser.getId()).orElse(loggedInUser);
        session.setAttribute("loggedInUser", freshUser); // update session

        List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(freshUser.getId());
        
        model.addAttribute("user", freshUser);
        model.addAttribute("orders", userOrders);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            HttpSession session) {
            
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        User userToUpdate = userService.getUserById(loggedInUser.getId()).orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setName(name);
            userToUpdate.setPhone(phone);
            userToUpdate.setAddress(address);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + profilePhoto.getOriginalFilename().replace(" ", "_");
                    Path path = Paths.get("src/main/resources/static/uploads/profiles/" + fileName);
                    Files.createDirectories(path.getParent());
                    Files.copy(profilePhoto.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    
                    Path targetPath = Paths.get("target/classes/static/uploads/profiles/" + fileName);
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(profilePhoto.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    userToUpdate.setProfilePhotoUrl("/uploads/profiles/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            User updatedUser = userService.updateUser(userToUpdate);
            session.setAttribute("loggedInUser", updatedUser);
        }

        return "redirect:/profile?success=true";
    }
}
