package com.example.hw.repository;

import com.example.hw.model.CategoriaCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaCarrinhoRepository extends JpaRepository<CategoriaCarrinho, Long> {
    List<CategoriaCarrinho> findAllByOrderByNomeAsc();
}
