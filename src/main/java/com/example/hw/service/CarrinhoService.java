package com.example.hw.service;

import com.example.hw.model.Carrinho;
import com.example.hw.model.CarrinhoDetailDTO;
import com.example.hw.model.CarrinhoListDTO;
import com.example.hw.repository.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    public List<Carrinho> findAll() {
        return carrinhoRepository.findAll();
    }

    public List<CarrinhoListDTO> findAllProjectedBy() {
        return carrinhoRepository.findAllProjectedBy();
    }

    public Optional<Carrinho> findById(Long id) {
        return carrinhoRepository.findById(id);
    }

    public Carrinho save(Carrinho carrinho) {
        return carrinhoRepository.save(carrinho);
    }

    public void deleteById(Long id) {
        carrinhoRepository.deleteById(id);
    }

    public CarrinhoDetailDTO getCarrinho(Long id) {
        Optional<Carrinho> c = carrinhoRepository.findById(id);
        if (c.isPresent()) {
            return new CarrinhoDetailDTO(c.get());
        }
        return null;
    }

    public List<CarrinhoListDTO> getCarrinhoByCodigo(String codigo) {
        return carrinhoRepository.findByCodigo(codigo);
    }

    public List<Carrinho> findFullCarrinhoByCodigo(String codigo) {
        return carrinhoRepository.findFullCarrinhoByCodigo(codigo);
    }

    public List<CarrinhoListDTO> findCarrinhosByDescricao(String s) {
        return carrinhoRepository.findByDescricao(s);
    }

    public List<CarrinhoListDTO> findFirst10ByOrderByDescricaoAsc() {
        return carrinhoRepository.findFirst10ByOrderByDescricaoAsc();
    }

    public List<CarrinhoListDTO> findByCodigoOrDescricao(String searchTerm) {
        return carrinhoRepository.findByCodigoOrDescricao(searchTerm);
    }

    public long countTotalCarrinhos() {
        return carrinhoRepository.count();
    }
}
