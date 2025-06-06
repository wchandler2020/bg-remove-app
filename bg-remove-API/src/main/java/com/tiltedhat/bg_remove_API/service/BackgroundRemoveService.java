package com.tiltedhat.bg_remove_API.service;

import org.springframework.web.multipart.MultipartFile;

public interface BackgroundRemoveService {
    byte[] removeBackground(MultipartFile file);
}
