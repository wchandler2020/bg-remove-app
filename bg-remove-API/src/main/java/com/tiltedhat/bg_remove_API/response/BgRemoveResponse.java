package com.tiltedhat.bg_remove_API.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BgRemoveResponse {
    private boolean success;
    private HttpStatus statusCode;
    private Object data;
}
