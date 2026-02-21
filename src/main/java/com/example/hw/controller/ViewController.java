package com.example.hw.controller;

import com.example.hw.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

@Controller
public class ViewController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/check")
    public String check() {
        return "check";
    }

    @GetMapping("/display")
    public String display() {
        return "display";
    }

    @GetMapping("/api/backup/sql")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> downloadBackup() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup_miniaturas.sql")
                .contentType(MediaType.TEXT_PLAIN)
                .body(outputStream -> {
                    try (java.io.Writer writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                        backupService.generateSqlDump(writer);
                    }
                });
    }
}
