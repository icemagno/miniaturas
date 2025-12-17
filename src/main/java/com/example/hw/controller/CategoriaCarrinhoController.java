package com.example.hw.controller;

import com.example.hw.model.CategoriaCarrinho;
import com.example.hw.service.CategoriaCarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

import com.example.hw.service.CategoryInUseException;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping(value = "/categoria-carrinho")
public class CategoriaCarrinhoController {

    @Autowired
    private CategoriaCarrinhoService categoriaCarrinhoService;

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    public List<CategoriaCarrinho> findAll() {
        return categoriaCarrinhoService.findAll();
    }

    @GetMapping(value = "/{id}", produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CategoriaCarrinho> findById(@PathVariable Long id) {
        return categoriaCarrinhoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    public CategoriaCarrinho save(@RequestBody CategoriaCarrinho categoriaCarrinho) {
        return categoriaCarrinhoService.save(categoriaCarrinho);
    }

    @PutMapping(value = "/{id}", produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CategoriaCarrinho> update(@PathVariable Long id, @RequestBody CategoriaCarrinho categoriaCarrinho) {
        return categoriaCarrinhoService.findById(id)
                .map(existingCategoria -> {
                    categoriaCarrinho.setId(id);
                    return ResponseEntity.ok(categoriaCarrinhoService.save(categoriaCarrinho));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            if (categoriaCarrinhoService.findById(id).isPresent()) {
                categoriaCarrinhoService.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (CategoryInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
