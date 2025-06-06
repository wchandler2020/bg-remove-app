package com.tiltedhat.bg_remove_API.controller;

import com.tiltedhat.bg_remove_API.dto.UserDTO;
import com.tiltedhat.bg_remove_API.response.BgRemoveResponse;
import com.tiltedhat.bg_remove_API.service.BackgroundRemoveService;
import com.tiltedhat.bg_remove_API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class BackgroundImageController {
    private final BackgroundRemoveService backgroundRemoveService;
    private final UserService userService;

    @PostMapping("/background-remove")
    public ResponseEntity<?> backgroundRemove(@RequestParam("file")MultipartFile file,
                                              Authentication authentication){
        System.out.println("THE API IS BEING CALLED!!!");
        BgRemoveResponse response = null;
        Map<String, Object> responseMap = new HashMap<>();
        try{
            // validation
            if(authentication.getName().isEmpty() || authentication.getName() == null){
                System.err.println("Authentication name is empty or null. User not authenticated or invalid token.");
                response = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("user does not have permission to access this resource")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDTO userDTO =  userService.getUserByClerkId(authentication.getName());
            //validation
            if(userDTO == null){
                System.err.println("User not found for Clerk ID: " + authentication.getName());
                response = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND)
                        .success(false)
                        .data("user not found")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if(userDTO.getCredits() == 0){
                System.err.println("Insufficient credit balance for user: " + authentication.getName());
                responseMap.put("message", "insuffecient credit balance.");
                responseMap.put("creditBalance", userDTO.getCredits());
                response = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .data(responseMap)
                        .success(false)
                        .build();
                return ResponseEntity.ok(response);
            }

            // Attempt to remove background
            byte[] imagebytes = backgroundRemoveService.removeBackground(file);

            // Validate if imagebytes are returned properly from the service
            if (imagebytes == null || imagebytes.length == 0) {
                System.err.println("Background removal service returned empty or null image bytes.");
                response = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.BAD_GATEWAY) // Or INTERNAL_SERVER_ERROR depending on cause
                        .success(false)
                        .data("failed to get processed image from background removal service")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
            }

            String base64Image = Base64.getEncoder().encodeToString(imagebytes);
            userDTO.setCredits(userDTO.getCredits() - 1);
            userService.saveUser((userDTO)); // Save updated user credits

            System.out.println("Background removal successful for user: " + authentication.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(base64Image);
        }catch(Exception e){
            // THIS IS THE CRUCIAL PART: Log the full stack trace of the exception
            System.err.println("ERROR during background removal API call:");
            e.printStackTrace(); // This will print the exception details to your server console

            response = BgRemoveResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("something went wrong please try again. Error: " + e.getMessage()) // Optionally, include a simplified error message
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
