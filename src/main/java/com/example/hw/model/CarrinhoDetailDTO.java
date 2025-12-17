package com.example.hw.model;

public class CarrinhoDetailDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private CategoriaCarrinho categoria;
    private String imagem;

    public CarrinhoDetailDTO(Carrinho carrinho) {
        this.id = carrinho.getId();
        this.codigo = carrinho.getCodigo();
        this.descricao = carrinho.getDescricao();
        this.categoria = carrinho.getCategoria();
        this.imagem = carrinho.getImagem();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public CategoriaCarrinho getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCarrinho categoria) {
        this.categoria = categoria;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
