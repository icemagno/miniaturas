package com.example.hw.repository;

import com.example.hw.model.DisplayCell;
import com.example.hw.model.DisplayCellDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisplayCellRepository extends JpaRepository<DisplayCell, Long> {
    List<DisplayCell> findByDisplayId(Long displayId);

    @Query("SELECT new com.example.hw.model.DisplayCellDTO(dc.id, dc.cellCode, c.id, c.codigo, c.descricao) FROM DisplayCell dc LEFT JOIN dc.carrinho c WHERE dc.display.id = :displayId")
    List<DisplayCellDTO> findProjectedByDisplayId(@Param("displayId") Long displayId);
    
    Optional<DisplayCell> findByDisplayIdAndCellCode(Long displayId, String cellCode);
    Optional<DisplayCell> findByCarrinhoId(Long carrinhoId);
}
