package com.springjobportal.job.portal.controller;

import com.springjobportal.job.portal.services.S3Service;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
public class TestController {

    private final S3Service s3Service;

    public TestController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/uploadtest")
    public String testUpload(@RequestParam("file") MultipartFile file) {

        String username = "ookk@gmail.com";
        String fileName = "";

        try {
            if (!Objects.equals(file.getOriginalFilename(), "")) {
                fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                s3Service.uploadFile(username, fileName, file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "login";
    }
}
