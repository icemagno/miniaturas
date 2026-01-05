package com.example.hw.model;

public class UpdateCellRequestDTO {
    private Long displayId;
    private String cellCode;
    private String newCarrinhoCode;

    // Getters and Setters
    public Long getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Long displayId) {
        this.displayId = displayId;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getNewCarrinhoCode() {
        return newCarrinhoCode;
    }

    public void setNewCarrinhoCode(String newCarrinhoCode) {
        this.newCarrinhoCode = newCarrinhoCode;
    }
}
