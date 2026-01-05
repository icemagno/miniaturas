package com.example.hw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "display")
public class Display {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String displayCode;

    @OneToMany(mappedBy = "display", cascade = CascadeType.ALL)
    private List<DisplayCell> cells;

    public Display() {
    }

    public Display(String displayCode) {
        this.displayCode = displayCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public List<DisplayCell> getCells() {
        return cells;
    }

    public void setCells(List<DisplayCell> cells) {
        this.cells = cells;
    }
}
