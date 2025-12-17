package com.example.hw.controller;

import com.example.hw.model.Carrinho;
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
    public CarrinhoDetailDTO save(@RequestBody Carrinho carrinho) {
        Carrinho savedCarrinho = carrinhoService.save(carrinho);
        return new CarrinhoDetailDTO(savedCarrinho);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrinhoDetailDTO> update(@PathVariable Long id, @RequestBody Carrinho carrinho) {
        return carrinhoService.findById(id)
                .map(existingCarrinho -> {
                    carrinho.setId(id);
                    Carrinho savedCarrinho = carrinhoService.save(carrinho);
                    return ResponseEntity.ok(new CarrinhoDetailDTO(savedCarrinho));
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
}
