package com.example.hw.config;

import com.example.hw.model.Display;
import com.example.hw.model.DisplayCell;
import com.example.hw.repository.DisplayRepository;
import com.example.hw.repository.DisplayCellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DisplayRepository displayRepository;

    @Autowired
    private DisplayCellRepository displayCellRepository;

    @Override
    public void run(String... args) throws Exception {
        if (displayRepository.count() == 0) {
            initializeDisplays();
        }
    }

    private void initializeDisplays() {
        String[] displayCodes = {"1L", "1R", "2L", "2R", "3L", "3R", "4L", "4R"};
        char[] rowCodes = "ABCDEFGHIJKLMNO".toCharArray();
        int numCols = 5;

        for (String displayCode : displayCodes) {
            Display display = new Display(displayCode);
            displayRepository.save(display);
            for (char rowCode : rowCodes) {
                for (int col = 1; col <= numCols; col++) {
                    String cellCode = rowCode + String.valueOf(col);
                    displayCellRepository.save(new DisplayCell(display, cellCode));
                }
            }
        }
    }
}
