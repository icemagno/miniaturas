package com.example.hw.model;

public class CarrinhoListDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private String categoriaNome;
    private Boolean checked;
    private String displayCode;
    private String cellCode;

    public CarrinhoListDTO(Long id, String codigo, String descricao, String categoriaNome, Boolean checked) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoriaNome = categoriaNome;
        this.checked = checked;
    }

    public CarrinhoListDTO(Long id, String codigo, String descricao, String categoriaNome, Boolean checked, String displayCode, String cellCode) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoriaNome = categoriaNome;
        this.checked = checked;
        this.displayCode = displayCode;
        this.cellCode = cellCode;
    }

    // Getters and setters
    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
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
}
