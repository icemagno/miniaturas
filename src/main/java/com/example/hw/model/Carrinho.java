package com.example.hw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carrinho")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaCarrinho categoria;

    @Column(name = "imagem", columnDefinition="TEXT")
    private String imagem;

    private Boolean checked;

    @jakarta.persistence.OneToOne(mappedBy = "carrinho")
    private DisplayCell displayCell;

    public Carrinho() {}

    public Carrinho(Long id, String codigo, String descricao, CategoriaCarrinho categoria) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoria = categoria;
    }

    // getters and setters

    public DisplayCell getDisplayCell() {
        return displayCell;
    }

    public void setDisplayCell(DisplayCell displayCell) {
        this.displayCell = displayCell;
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
