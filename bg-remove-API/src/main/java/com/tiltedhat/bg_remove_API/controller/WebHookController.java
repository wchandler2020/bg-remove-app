package com.tiltedhat.bg_remove_API.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svix.Webhook;
import com.svix.exceptions.ApiException;
import com.tiltedhat.bg_remove_API.dto.UserDTO;
import com.tiltedhat.bg_remove_API.model.UserEntity;
import com.tiltedhat.bg_remove_API.response.BgRemoveResponse;
import com.tiltedhat.bg_remove_API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebHookController {
    @Value("${clerk.secret}")
    private String webhookSecret;

    private final UserService userService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timeStamp" ) String svixTimeStamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload){
        BgRemoveResponse response = null;
        try{
            boolean isValid = verifyWebhookSignature(svixId, svixTimeStamp, svixSignature, payload);
            if(!isValid){
                response = BgRemoveResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .data("Invalid Webhook Signature")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();
            switch (eventType){
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            response = BgRemoveResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
//        return ResponseEntity.ok("Webhook verified");
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        userService.deleteUserByClerkId(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId =  data.path("id").asText();
        UserDTO existingUser = userService.getUserByClerkId(clerkId);
        existingUser.setEmail(data.path("email_addresses").path(0).path("email_address").asText());
        existingUser.setFirstName(data.path("first_name").asText());
        existingUser.setLastName(data.path("last_name").asText());
        existingUser.setPhotoUrl(data.path("image_url").asText());
        userService.saveUser(existingUser);
    }

    private void handleUserCreated(JsonNode data) {
        UserDTO newUser = UserDTO.builder()
                .clerkId(data.path("id").asText())
                .email(data.path("email_addresses").path(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .build();
        userService.saveUser(newUser);
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimeStamp, String svixSignature, String payload) throws ApiException {
//        Webhook wh = new Webhook(webhookSecret);
//        wh.verify(payload, svixSignature);
        return true;
    }
}
