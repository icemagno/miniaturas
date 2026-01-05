package com.example.hw.controller;

import com.example.hw.model.DisplayCellDTO;
import com.example.hw.model.DisplayDTO;
import com.example.hw.repository.DisplayCellRepository;
import com.example.hw.repository.DisplayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/displays")
public class DisplayController {

    @Autowired
    private DisplayRepository displayRepository;

    @Autowired
    private DisplayCellRepository displayCellRepository;

    @GetMapping
    public List<DisplayDTO> findAll() {
        return displayRepository.findAll().stream()
                .map(display -> new DisplayDTO(display.getId(), display.getDisplayCode()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{displayId}/cells")
    public List<DisplayCellDTO> findCellsByDisplayId(@PathVariable Long displayId) {
        return displayCellRepository.findByDisplayId(displayId).stream()
                .map(cell -> new DisplayCellDTO(cell.getId(), cell.getCellCode(), cell.getCarrinho()))
                .collect(Collectors.toList());
    }
}
