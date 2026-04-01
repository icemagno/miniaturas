package com.example.hw.controller;

import com.example.hw.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @Autowired
    private com.example.hw.service.CarrinhoService carrinhoService;

    @GetMapping("/getimage/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return carrinhoService.findById(id)
                .map(carrinho -> {
                    String base64Image = carrinho.getImagem();
                    if (base64Image != null) {
                        try {
                            String base64Data;
                            String contentType = "image/jpeg";
                            if (base64Image.contains(",")) {
                                String[] parts = base64Image.split(",");
                                base64Data = parts[1];
                                contentType = parts[0].split(":")[1].split(";")[0];
                            } else {
                                base64Data = base64Image;
                            }
                            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                            return ResponseEntity.ok()
                                    .contentType(MediaType.parseMediaType(contentType))
                                    .body(imageBytes);
                        } catch (Exception e) {
                            return ResponseEntity.status(500).<byte[]>build();
                        }
                    }
                    return ResponseEntity.notFound().<byte[]>build();
                })
                .orElse(ResponseEntity.notFound().build());
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
