package com.tiltedhat.bg_remove_API.controller;


import com.tiltedhat.bg_remove_API.dto.UserDTO;
import com.tiltedhat.bg_remove_API.response.BgRemoveResponse;
import com.tiltedhat.bg_remove_API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDTO userDTO, Authentication authentication){
        BgRemoveResponse response =null;
        try{
            if(!authentication.getName().equals(userDTO.getClerkId())){

                response = BgRemoveResponse.builder()
                        .success(false)
                        .data("User Unathorized")
                        .statusCode(HttpStatus.FORBIDDEN)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDTO user = userService.saveUser(userDTO);
            response = BgRemoveResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch(Exception exception){
            response = BgRemoveResponse.builder()
                    .success(false)
                    .data(exception.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(Authentication authentication){
        BgRemoveResponse bgResponse = null;
        try{
            if(authentication == null || authentication.getName() == null || authentication.getName().isEmpty()){
                System.out.println("Authentication details missing or empty.");
                bgResponse = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN) // More appropriate than FORBIDDEN
                        .data("Authentication required or user ID missing.")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(bgResponse);
            }

            String clerkId = authentication.getName();
            UserDTO existingUser = userService.getUserByClerkId(clerkId);
            Map<String, Integer> map = new HashMap<>();
            map.put("credits", existingUser.getCredits());
            bgResponse = BgRemoveResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .data(map)
                    .success(true)
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bgResponse);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to see the exact line of error
            bgResponse = BgRemoveResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR) // 500 for true internal errors
                    .data("An unexpected error occurred while fetching credits.") // More specific message
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(bgResponse);
        }
    }
}
