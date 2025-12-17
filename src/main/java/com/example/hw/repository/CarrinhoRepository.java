package com.example.hw.repository;

import com.example.hw.model.Carrinho;
import com.example.hw.model.CarrinhoListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    
    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao) FROM Carrinho c WHERE c.codigo = :codigo")
    List<CarrinhoListDTO> findByCodigo(@Param("codigo") String codigo);

    List<Carrinho> findFullCarrinhoByCodigo(String codigo);
    
    long countByCategoriaId(Long categoriaId);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao) FROM Carrinho c WHERE upper(c.descricao) like upper(concat('%', :s, '%'))")
    List<CarrinhoListDTO> findByDescricao(@Param("s") String s);    

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao) FROM Carrinho c ORDER BY c.descricao ASC LIMIT 10")
    List<CarrinhoListDTO> findFirst10ByOrderByDescricaoAsc();

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao) FROM Carrinho c WHERE upper(c.descricao) LIKE upper(concat('%', :searchTerm, '%')) OR upper(c.codigo) LIKE upper(concat('%', :searchTerm, '%')) ORDER BY c.descricao ASC")
    List<CarrinhoListDTO> findByCodigoOrDescricao(@Param("searchTerm") String searchTerm);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao) FROM Carrinho c")
    List<CarrinhoListDTO> findAllProjectedBy();

    long count();
}
