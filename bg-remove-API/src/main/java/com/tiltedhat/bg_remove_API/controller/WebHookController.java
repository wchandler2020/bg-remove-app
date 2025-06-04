package com.tiltedhat.bg_remove_API.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svix.Webhook;
import com.svix.exceptions.ApiException;
import com.tiltedhat.bg_remove_API.response.BgRemoveResponse;
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

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-header" ) String svixTimeStamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload){
        try{
            boolean isValid = verifyWebhookSignature(svixId, svixTimeStamp, svixSignature, payload);
            if(!isValid){
                BgRemoveResponse response = BgRemoveResponse.builder()
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
        }catch (Exception e){
            System.out.println(e);
        }
        return ResponseEntity.ok("Webhook verified");
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimeStamp, String svixSignature, String payload) throws ApiException {
//        Webhook wh = new Webhook(webhookSecret);
//        wh.verify(payload, svixSignature);
        return true;
    }
}
