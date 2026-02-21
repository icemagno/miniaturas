package com.example.hw.repository;

import com.example.hw.model.Carrinho;
import com.example.hw.model.CarrinhoListDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    
    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d " +
           "WHERE c.codigo = :codigo")
    List<CarrinhoListDTO> findByCodigo(@Param("codigo") String codigo);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d " +
           "WHERE upper(c.codigo) LIKE upper(concat(:code, '%')) ORDER BY c.codigo ASC")
    List<CarrinhoListDTO> findByCodigoStartsWith(@Param("code") String code, Pageable pageable);

    List<Carrinho> findFullCarrinhoByCodigo(String codigo);
    
    long countByCategoriaId(Long categoriaId);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d " +
           "WHERE upper(c.descricao) like upper(concat('%', :s, '%'))")
    List<CarrinhoListDTO> findByDescricao(@Param("s") String s);    

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d " +
           "ORDER BY c.descricao ASC")
    List<CarrinhoListDTO> findFirst10ByOrderByDescricaoAsc(Pageable pageable);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d " +
           "WHERE upper(c.descricao) LIKE upper(concat('%', :searchTerm, '%')) OR upper(c.codigo) LIKE upper(concat('%', :searchTerm, '%')) " +
           "ORDER BY c.descricao ASC")
    List<CarrinhoListDTO> findByCodigoOrDescricao(@Param("searchTerm") String searchTerm);

    @Query("SELECT new com.example.hw.model.CarrinhoListDTO(c.id, c.codigo, c.descricao, cat.nome, c.checked, d.displayCode, dc.cellCode) " +
           "FROM Carrinho c LEFT JOIN c.categoria cat LEFT JOIN c.displayCell dc LEFT JOIN dc.display d")
    List<CarrinhoListDTO> findAllProjectedBy();

    List<Carrinho> findAllByOrderByDescricaoAsc();

    long count();

    List<Carrinho> findByCheckedFalse();

    List<Carrinho> findByCheckedIsNullOrCheckedIsFalse();

    List<Carrinho> findByCheckedTrue();

    @Modifying
    @Query("UPDATE Carrinho c SET c.checked = false")
    void resetAllChecked();
}
