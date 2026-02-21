package com.example.hw.service;

import com.example.hw.model.*;
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
        return displayCellRepository.findProjectedByDisplayId(displayId);
    }

    @Transactional
    public UpdateCellResponseDTO updateCell(UpdateCellRequestDTO request) {
        Optional<DisplayCell> cellOptional = displayCellRepository.findByDisplayIdAndCellCode(request.getDisplayId(), request.getCellCode());
        if (cellOptional.isEmpty()) {
            return new UpdateCellResponseDTO("ERROR", "Célula não encontrada.");
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
            return new UpdateCellResponseDTO("SUCCESS", "Célula esvaziada.");
        }

        // Case 2: A new code is provided
        List<Carrinho> carrinhos = carrinhoRepository.findFullCarrinhoByCodigo(newCode);
        if (carrinhos.isEmpty()) {
            return new UpdateCellResponseDTO("ERROR", "Miniatura com o código '" + newCode + "' não encontrada.");
        }
        Carrinho newCarrinho = carrinhos.get(0);
        
        // If the new carrinho is already checked, abort and notify
        if (newCarrinho.getChecked() != null && newCarrinho.getChecked()) {
            // Also ensure it's not the same carrinho already in this cell
            if (originalCarrinho != null && originalCarrinho.getId().equals(newCarrinho.getId())) {
                // If it's the same carrinho, and it's already checked, just return SUCCESS
                return new UpdateCellResponseDTO("SUCCESS", "Miniatura '" + newCode + "' já está nesta célula e checada.");
            }
            // Find the cell where this carrinho is currently displayed
            Optional<DisplayCell> occupiedCellOptional = displayCellRepository.findByCarrinhoId(newCarrinho.getId());
            if (occupiedCellOptional.isPresent()) {
                DisplayCell occupiedCell = occupiedCellOptional.get();
                String displayCode = occupiedCell.getDisplay().getDisplayCode();
                String cellCode = occupiedCell.getCellCode();
                return new UpdateCellResponseDTO("ALREADY_CHECKED", "Esta miniatura já está em exposição.", displayCode, cellCode);
            } else {
                // This case should ideally not happen if checked==true, but as a fallback:
                return new UpdateCellResponseDTO("ALREADY_CHECKED", "Esta miniatura já está marcada como em exposição, mas não foi possível encontrar a sua célula.", null, null);
            }
        }
        
        // If the cell had a different carrinho, uncheck it
        if (originalCarrinho != null && !originalCarrinho.getId().equals(newCarrinho.getId())) {
            originalCarrinho.setChecked(false);
            carrinhoRepository.save(originalCarrinho);
        }

        // Check the new carrinho and associate it with the cell
        newCarrinho.setChecked(true);
        carrinhoRepository.save(newCarrinho);
        
        cell.setCarrinho(newCarrinho);
        newCarrinho.setDisplayCell(cell); // Set the inverse side
        displayCellRepository.save(cell);
        
        return new UpdateCellResponseDTO("SUCCESS", "Célula atualizada com sucesso.");
    }
}