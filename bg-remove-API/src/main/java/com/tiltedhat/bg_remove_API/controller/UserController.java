package com.tiltedhat.bg_remove_API.controller;


import com.tiltedhat.bg_remove_API.dto.UserDTO;
import com.tiltedhat.bg_remove_API.response.BgRemoveResponse;
import com.tiltedhat.bg_remove_API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public BgRemoveResponse createOrUpdateUser(@RequestBody UserDTO userDTO){
        try{
            UserDTO user = userService.saveUser(userDTO);
            return BgRemoveResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.CREATED)
                    .build();
        }catch(Exception exception){
            return BgRemoveResponse.builder()
                    .success(false)
                    .data(exception.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

    }
}
