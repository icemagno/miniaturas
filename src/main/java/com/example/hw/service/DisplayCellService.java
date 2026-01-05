package com.example.hw.service;

import com.example.hw.repository.DisplayCellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisplayCellService {

    @Autowired
    private DisplayCellRepository displayCellRepository;

}
