package com.example.hw.model;

public class DisplayDTO {
    private Long id;
    private String displayCode;

    public DisplayDTO(Long id, String displayCode) {
        this.id = id;
        this.displayCode = displayCode;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }
}
