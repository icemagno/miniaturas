package com.example.hw.service;

import com.example.hw.repository.DisplayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisplayService {

    @Autowired
    private DisplayRepository displayRepository;

}
