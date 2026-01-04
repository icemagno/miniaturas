package com.example.hw.model;

public class CarrinhoListDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private Boolean checked;

    public CarrinhoListDTO(Long id, String codigo, String descricao, Boolean checked) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.checked = checked;
    }

    // Getters and setters
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
}
