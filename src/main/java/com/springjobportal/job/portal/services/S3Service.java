package com.springjobportal.job.portal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public String uploadFile(String userId, String fileName, MultipartFile fileData) throws IOException {

        try {
            String key = "users/" + userId + "/" + fileName;

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(fileData.getBytes()));

            return fileName;
        } catch (Exception e) {
            throw new IOException("upload failed: " + e.getMessage(), e);
        }
    }


    public byte[] downloadFile(String userId, String fileName) {

        try {

            String key = "users/" + userId + "/" + fileName;

            return s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            ).asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("download failed: " + e.getMessage(), e);
        }
    }
}
