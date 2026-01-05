package com.example.hw.model;

public class UpdateCellResponseDTO {
    private String status;
    private String message;
    private String currentDisplayCode;
    private String currentCellCode;

    public UpdateCellResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public UpdateCellResponseDTO(String status, String message, String currentDisplayCode, String currentCellCode) {
        this.status = status;
        this.message = message;
        this.currentDisplayCode = currentDisplayCode;
        this.currentCellCode = currentCellCode;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCurrentDisplayCode() { return currentDisplayCode; }
    public void setCurrentDisplayCode(String currentDisplayCode) { this.currentDisplayCode = currentDisplayCode; }
    public String getCurrentCellCode() { return currentCellCode; }
    public void setCurrentCellCode(String currentCellCode) { this.currentCellCode = currentCellCode; }
}
