package com.example.hw.repository;

import com.example.hw.model.DisplayCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisplayCellRepository extends JpaRepository<DisplayCell, Long> {
    List<DisplayCell> findByDisplayId(Long displayId);
    Optional<DisplayCell> findByDisplayIdAndCellCode(Long displayId, String cellCode);
}
