package com.example.hw.service;

import com.example.hw.model.*;
import com.example.hw.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class BackupService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CategoriaCarrinhoRepository categoriaRepository;

    @Autowired
    private DisplayCellRepository displayCellRepository;

    @Transactional(readOnly = true)
    public void generateSqlDump(Writer writer) throws IOException {
        writer.write("-- Backup de Miniaturas\n");
        writer.write("-- Gerado via JPA BackupService (Streaming Mode)\n\n");
        writer.write("BEGIN;\n\n");

        // 1. Categories
        writer.write("-- Categorias\n");
        List<CategoriaCarrinho> categorias = categoriaRepository.findAll();
        for (CategoriaCarrinho cat : categorias) {
            writer.write(String.format(
                "INSERT INTO categoria_carrinho (id, nome, descricao) VALUES (%d, '%s', '%s') ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, descricao = EXCLUDED.descricao;\n",
                cat.getId(), escapeSql(cat.getNome()), escapeSql(cat.getDescricao())
            ));
        }
        writer.write("\n");
        writer.flush(); // Send categories to browser first

        // 2. Carrinhos (Items) - This is where the images are
        writer.write("-- Miniaturas (incluindo imagens Base64)\n");
        // Using a stream to avoid loading everything into memory if the driver supports it
        // For 600 items, a list is okay, but we process them one by one to the writer
        List<Carrinho> carrinhos = carrinhoRepository.findAll();
        for (Carrinho c : carrinhos) {
            String catId = (c.getCategoria() != null) ? c.getCategoria().getId().toString() : "NULL";
            String checked = (c.getChecked() != null && c.getChecked()) ? "TRUE" : "FALSE";
            String imagem = (c.getImagem() != null) ? "'" + escapeSql(c.getImagem()) + "'" : "NULL";
            
            writer.write(String.format(
                "INSERT INTO carrinho (id, codigo, descricao, categoria_id, checked, imagem) VALUES (%d, '%s', '%s', %s, %s, %s) ON CONFLICT (id) DO UPDATE SET codigo = EXCLUDED.codigo, descricao = EXCLUDED.descricao, categoria_id = EXCLUDED.categoria_id, checked = EXCLUDED.checked, imagem = EXCLUDED.imagem;\n",
                c.getId(), escapeSql(c.getCodigo()), escapeSql(c.getDescricao()), catId, checked, imagem
            ));
            writer.flush(); // Send each item immediately to avoid memory buildup
        }
        writer.write("\n");

        // 3. Display Cells (Updates associations)
        writer.write("-- Associacoes de Celulas\n");
        List<DisplayCell> cells = displayCellRepository.findAll();
        for (DisplayCell cell : cells) {
            if (cell.getCarrinho() != null) {
                writer.write(String.format(
                    "UPDATE display_cell SET carrinho_id = %d WHERE cell_code = '%s' AND display_id = %d;\n",
                    cell.getCarrinho().getId(), escapeSql(cell.getCellCode()), cell.getDisplay().getId()
                ));
            }
        }

        writer.write("\nCOMMIT;");
        writer.flush();
    }

    private String escapeSql(String str) {
        if (str == null) return "";
        return str.replace("'", "''");
    }
}
