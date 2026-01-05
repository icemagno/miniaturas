package com.example.hw.controller;

import com.example.hw.model.*;
import com.example.hw.repository.DisplayRepository;
import com.example.hw.service.DisplayService;
import com.example.hw.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/displays")
public class DisplayController {

    @Autowired
    private DisplayRepository displayRepository;

    @Autowired
    private DisplayService displayService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public List<DisplayDTO> findAll() {
        return displayRepository.findAll().stream()
                .map(display -> new DisplayDTO(display.getId(), display.getDisplayCode()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{displayId}/cells")
    public List<DisplayCellDTO> findCellsByDisplayId(@PathVariable Long displayId) {
        return displayService.findCellsByDisplayId(displayId);
    }

    @PostMapping("/cells/update")
    public ResponseEntity<UpdateCellResponseDTO> updateCell(@RequestBody UpdateCellRequestDTO request) {
        UpdateCellResponseDTO response = displayService.updateCell(request);
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{displayId}/export/pdf")
    public ResponseEntity<byte[]> exportDisplayPdf(@PathVariable Long displayId) {
        byte[] pdf = pdfService.createDisplayPdf(displayId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=display_" + displayId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

