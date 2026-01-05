package com.example.hw.repository;

import com.example.hw.model.DisplayCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayCellRepository extends JpaRepository<DisplayCell, Long> {
    List<DisplayCell> findByDisplayId(Long displayId);
}
