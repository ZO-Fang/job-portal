package com.springjobportal.job.portal.util;

import org.springframework.core.io.Resource;   // ✅ 用Spring的Resource
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    private Path foundfile;

    public Resource getFileAsResourse(String downloadDir, String fileName) throws IOException {
        Path path = Paths.get(downloadDir);

        //遍历目录 → 找到以某个名字开头的文件 → 返回它。
        Files.list(path).forEach(file -> {
            if (file.getFileName().toString().startsWith(fileName)) {
                foundfile = file;
            }
        });

        if (foundfile != null) {
            return new UrlResource(foundfile.toUri());  // UrlResource 实现了 Resource
        }

        return null;
    }
}
