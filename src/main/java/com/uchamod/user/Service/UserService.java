package com.uchamod.user.Service;

import com.uchamod.user.Model.User;
import com.uchamod.user.Model.UserWrapper;
import com.uchamod.user.Reposotory.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    //get all users
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            List<User> users=userRepo.findAll();
            List<UserWrapper> userWrappers=users.stream()
                    .map(user -> new UserWrapper(
                            user.getUserName(),
                            user.getUserEmail(),
                            user.getUserRole(),
                            user.getUserStatus(),
                            user.getUserPhone(),
                            user.getUserAddress()
                    )).collect(Collectors.toList());
            return  ResponseEntity.ok(userWrappers);
        }catch (Exception e){
            System.out.println("faild to get all users");
            return ResponseEntity.internalServerError().build();
        }
    }
   //get user by email
    public ResponseEntity<UserWrapper> getUserByEmail(String email) {
        try{
            User user=userRepo.findByUserEmail(email);
            UserWrapper userWrapper=new UserWrapper(
                    user.getUserName(),
                    user.getUserAddress(),
                    user.getUserRole(),
                    user.getUserStatus(),
                    user.getUserPhone(),
                    user.getUserEmail()
            );
            return ResponseEntity.ok(userWrapper);
        }catch (Exception e){
            System.out.println("failed to get user data"+e.getMessage());
           return ResponseEntity.internalServerError().build();
        }
    }
}
