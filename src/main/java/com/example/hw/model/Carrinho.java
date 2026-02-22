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

    @jakarta.persistence.Transient
    private String displayCode;
    @jakarta.persistence.Transient
    private String cellCode;

    public Carrinho() {}

    public Carrinho(Long id, String codigo, String descricao, CategoriaCarrinho categoria) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public Carrinho(Long id, String codigo, String descricao, CategoriaCarrinho categoria, String displayCode, String cellCode) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.displayCode = displayCode;
        this.cellCode = cellCode;
    }

    // getters and setters

    public String getDisplayCode() {
        if (displayCode != null) return displayCode;
        if (displayCell != null && displayCell.getDisplay() != null) {
            return displayCell.getDisplay().getDisplayCode();
        }
        return null;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getCellCode() {
        if (cellCode != null) return cellCode;
        if (displayCell != null) {
            return displayCell.getCellCode();
        }
        return null;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

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
