package com.tiltedhat.bg_remove_API.service;

import com.tiltedhat.bg_remove_API.client.ClipdropClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BackgroundRemoveImplementation implements BackgroundRemoveService{
    @Value("${clipdrop.api.key}")
    private String apikey;

    private final ClipdropClient clipdropClient;
    @Override
    public byte[] removeBackground(MultipartFile file) {
        System.out.println(apikey);
        return clipdropClient.removeBackground(file, apikey);
    }
}
