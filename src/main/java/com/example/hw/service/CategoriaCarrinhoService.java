package com.example.hw.service;

import com.example.hw.model.CategoriaCarrinho;
import com.example.hw.repository.CategoriaCarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaCarrinhoService {

    @Autowired
    private CategoriaCarrinhoRepository categoriaCarrinhoRepository;

    @Autowired
    private com.example.hw.repository.CarrinhoRepository carrinhoRepository;

    public List<CategoriaCarrinho> findAll() {
        return categoriaCarrinhoRepository.findAllByOrderByNomeAsc();
    }

    public Optional<CategoriaCarrinho> findById(Long id) {
        return categoriaCarrinhoRepository.findById(id);
    }

    public CategoriaCarrinho save(CategoriaCarrinho categoriaCarrinho) {
        return categoriaCarrinhoRepository.save(categoriaCarrinho);
    }

    public void deleteById(Long id) {
        if (carrinhoRepository.countByCategoriaId(id) > 0) {
            throw new CategoryInUseException("Categoria em uso e não pode ser excluída.");
        }
        categoriaCarrinhoRepository.deleteById(id);
    }
}
