package com.example.hw.model;

public class DisplayCellDTO {
    private Long id;
    private String cellCode;
    private CarrinhoInCellDTO carrinho;

    public DisplayCellDTO(Long id, String cellCode, Carrinho carrinho) {
        this.id = id;
        this.cellCode = cellCode;
        if (carrinho != null) {
            this.carrinho = new CarrinhoInCellDTO(carrinho.getId(), carrinho.getCodigo(), carrinho.getDescricao());
        } else {
            this.carrinho = null;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }
    public CarrinhoInCellDTO getCarrinho() { return carrinho; }
    public void setCarrinho(CarrinhoInCellDTO carrinho) { this.carrinho = carrinho; }
}
