package com.example.hw.service;

import com.example.hw.model.Carrinho;
import com.example.hw.model.DisplayCell;
import com.example.hw.model.DisplayCellDTO;
import com.example.hw.model.UpdateCellRequestDTO;
import com.example.hw.repository.CarrinhoRepository;
import com.example.hw.repository.DisplayCellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisplayService {

    @Autowired
    private DisplayCellRepository displayCellRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    public List<DisplayCellDTO> findCellsByDisplayId(Long displayId) {
        return displayCellRepository.findByDisplayId(displayId).stream()
                .map(cell -> new DisplayCellDTO(cell.getId(), cell.getCellCode(), cell.getCarrinho()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean updateCell(UpdateCellRequestDTO request) {
        Optional<DisplayCell> cellOptional = displayCellRepository.findByDisplayIdAndCellCode(request.getDisplayId(), request.getCellCode());
        if (cellOptional.isEmpty()) {
            return false; // Cell not found
        }

        DisplayCell cell = cellOptional.get();
        Carrinho originalCarrinho = cell.getCarrinho();
        String newCode = request.getNewCarrinhoCode();

        // Case 1: Code is cleared, so uncheck the original carrinho
        if (newCode == null || newCode.trim().isEmpty()) {
            if (originalCarrinho != null) {
                originalCarrinho.setChecked(false);
                carrinhoRepository.save(originalCarrinho);
            }
            cell.setCarrinho(null);
            displayCellRepository.save(cell);
            return true;
        }

        // Case 2: A new code is provided
        List<Carrinho> carrinhos = carrinhoRepository.findFullCarrinhoByCodigo(newCode);
        if (carrinhos.isEmpty()) {
            return false; // Carrinho with the new code not found
        }
        Carrinho newCarrinho = carrinhos.get(0);

        // If the cell had a different carrinho, uncheck it
        if (originalCarrinho != null && !originalCarrinho.getId().equals(newCarrinho.getId())) {
            originalCarrinho.setChecked(false);
            carrinhoRepository.save(originalCarrinho);
        }

        // Check the new carrinho and associate it with the cell
        newCarrinho.setChecked(true);
        carrinhoRepository.save(newCarrinho);
        
        cell.setCarrinho(newCarrinho);
        displayCellRepository.save(cell);
        return true;
    }
}