package com.uchamod.user.Controller;

import com.uchamod.user.Model.UserWrapper;
import com.uchamod.user.Service.AuthServices;
import com.uchamod.user.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

   private final UserService userServices;
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserWrapper>> getAllUsers(){
        return userServices.getAllUsers();
    }

    @GetMapping("/getUserByEmail")
    public ResponseEntity<UserWrapper> getUserByEmail(Authentication authentication){
       String email= authentication.getName();
        return userServices.getUserByEmail(email);
    }
}
