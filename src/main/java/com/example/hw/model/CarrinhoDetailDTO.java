package com.example.hw.model;

public class CarrinhoDetailDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private CategoriaCarrinho categoria;
    private String imagem;
    private Boolean checked;
    private String displayCode;
    private String cellCode;

    public CarrinhoDetailDTO(Carrinho carrinho) {
        this.id = carrinho.getId();
        this.codigo = carrinho.getCodigo();
        this.descricao = carrinho.getDescricao();
        this.categoria = carrinho.getCategoria();
        this.imagem = carrinho.getImagem();
        this.checked = carrinho.getChecked();
        if (carrinho.getDisplayCell() != null) {
            this.cellCode = carrinho.getDisplayCell().getCellCode();
            if (carrinho.getDisplayCell().getDisplay() != null) {
                this.displayCode = carrinho.getDisplayCell().getDisplay().getDisplayCode();
            }
        }
    }

    // Getters and setters

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

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
