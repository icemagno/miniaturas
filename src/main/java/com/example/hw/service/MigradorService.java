package com.example.hw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import com.example.hw.model.Carrinho;

@Service
public class MigradorService {

    @Autowired
    private CarrinhoService carrinhoService;

    //@PostConstruct
    private void init() {
        System.out.println("Iniciando MigradorService...");
        File folder = new File("/carrinhos/images/2026");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            System.out.println("Não foi possível encontrar o diretório /carrinhos/images ou ele está vazio.");
            return;
        }

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                String codigo = fileName.substring(0, fileName.lastIndexOf('.'));
                System.out.println("Processando arquivo: " + fileName + ", código: " + codigo);

                List<Carrinho> carrinhos = carrinhoService.findFullCarrinhoByCodigo(codigo);
                if (carrinhos.isEmpty()) {
                    System.out.println("Nenhum carrinho encontrado para o código: " + codigo + ". Pulando.");
                    continue;
                }

                Carrinho carrinho = carrinhos.get(0);

                try {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    String base64Image = "data:image/jpeg;base64," + encodedString;
                    carrinho.setImagem(base64Image);
                    carrinhoService.save(carrinho);
                    System.out.println("Imagem para o carrinho " + codigo + " foi atualizada.");
                } catch (IOException e) {
                    System.err.println("Erro ao ler o arquivo de imagem: " + fileName);
                    e.printStackTrace();
                }
            }
        }
        System.out.println("MigradorService finalizado.");
    }
}
