package com.example.hw.model;

public class CarrinhoCheckResponseDTO {

    private CarrinhoListDTO carrinho;
    private boolean alreadyChecked;

    public CarrinhoCheckResponseDTO(CarrinhoListDTO carrinho, boolean alreadyChecked) {
        this.carrinho = carrinho;
        this.alreadyChecked = alreadyChecked;
    }

    public CarrinhoListDTO getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(CarrinhoListDTO carrinho) {
        this.carrinho = carrinho;
    }

    public boolean isAlreadyChecked() {
        return alreadyChecked;
    }

    public void setAlreadyChecked(boolean alreadyChecked) {
        this.alreadyChecked = alreadyChecked;
    }
}
