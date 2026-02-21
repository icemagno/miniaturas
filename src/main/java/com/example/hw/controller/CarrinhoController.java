package com.example.hw.controller;

import com.example.hw.model.Carrinho;
import com.example.hw.model.CarrinhoCheckResponseDTO;
import com.example.hw.model.CarrinhoDetailDTO;
import com.example.hw.model.CarrinhoListDTO;
import com.example.hw.service.CarrinhoService;
import com.example.hw.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping(value = "/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        List<Carrinho> carrinhos = carrinhoService.findAll();
        byte[] pdf = pdfService.createPdf(carrinhos);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=carrinhos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/pdf/alphabetical")
    public ResponseEntity<byte[]> exportPdfAlphabetical() {
        List<Carrinho> carrinhos = carrinhoService.findAllAlphabetically();
        byte[] pdf = pdfService.createAlphabeticalPdf(carrinhos);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=carrinhos-alfabetico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/pdf/unchecked")
    public ResponseEntity<byte[]> exportPdfUnchecked() {
        List<Carrinho> carrinhos = carrinhoService.findAllUnchecked();
        byte[] pdf = pdfService.createUncheckedPdf(carrinhos);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=carrinhos-nao-checados.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/pdf/checked")
    public ResponseEntity<byte[]> exportPdfChecked() {
        List<Carrinho> carrinhos = carrinhoService.findAllChecked();
        byte[] pdf = pdfService.createPdf(carrinhos);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=carrinhos-checados.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/pdf/pictures")
    public ResponseEntity<byte[]> exportPdfWithPictures() {
        List<Carrinho> carrinhos = carrinhoService.findAll();
        byte[] pdf = pdfService.createPdfWithPictures(carrinhos);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=carrinhos-com-fotos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping
    public List<CarrinhoListDTO> findAll() {
        return carrinhoService.findAllProjectedBy();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody CarrinhoDetailDTO findById(@PathVariable Long id) {
        return carrinhoService.getCarrinho(id);
    }

    @GetMapping(value = "/bycode/{codigo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<CarrinhoListDTO> findByCodigo(@PathVariable String codigo) {
        return carrinhoService.getCarrinhoByCodigo(codigo);
    }

    @GetMapping(value = "/autocomplete/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<CarrinhoListDTO> autocompleteByCode(@PathVariable String code) {
        return carrinhoService.findByCodigoStartsWith(code);
    }

    @GetMapping(value = "/search/{searchTerm}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<CarrinhoListDTO> unifiedSearch(@PathVariable String searchTerm) {
        return carrinhoService.findByCodigoOrDescricao(searchTerm);
    }

    @GetMapping(value = "/initial", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<CarrinhoListDTO> findInitial() {
        return carrinhoService.findFirst10ByOrderByDescricaoAsc();
    }

    @GetMapping(value = "/count")
    public long count() {
        return carrinhoService.countTotalCarrinhos();
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Carrinho carrinho) {
        if (!carrinhoService.findFullCarrinhoByCodigo(carrinho.getCodigo()).isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: Já existe uma miniatura cadastrada com o código " + carrinho.getCodigo());
        }
        Carrinho savedCarrinho = carrinhoService.save(carrinho);
        return ResponseEntity.ok(new CarrinhoDetailDTO(savedCarrinho));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Carrinho carrinho) {
        return carrinhoService.findById(id)
                .map(existing -> {
                    // Check if new code belongs to ANOTHER item
                    List<Carrinho> conflicting = carrinhoService.findFullCarrinhoByCodigo(carrinho.getCodigo());
                    if (!conflicting.isEmpty() && !conflicting.get(0).getId().equals(id)) {
                        return ResponseEntity.badRequest().body("Erro: O código " + carrinho.getCodigo() + " já está sendo usado por outra miniatura.");
                    }
                    
                    existing.setCodigo(carrinho.getCodigo());
                    existing.setDescricao(carrinho.getDescricao());
                    existing.setCategoria(carrinho.getCategoria());
                    existing.setChecked(carrinho.getChecked());
                    existing.setImagem(carrinho.getImagem());
                    Carrinho saved = carrinhoService.save(existing);
                    return ResponseEntity.ok(new CarrinhoDetailDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (carrinhoService.findById(id).isPresent()) {
            carrinhoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/check/{codigo}")
    public ResponseEntity<CarrinhoCheckResponseDTO> checkCarrinho(@PathVariable String codigo) {
        return carrinhoService.checkCarrinho(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-checked")
    public ResponseEntity<Void> resetAllChecked() {
        carrinhoService.resetAllChecked();
        return ResponseEntity.noContent().build();
    }
}
