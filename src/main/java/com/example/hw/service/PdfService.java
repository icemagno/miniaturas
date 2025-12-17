package com.example.hw.service;

import com.example.hw.model.Carrinho;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public byte[] createPdf(List<Carrinho> carrinhos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font categoryFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12);
            Font itemFont = FontFactory.getFont(FontFactory.COURIER, 10);

            Map<String, List<Carrinho>> groupedByCategory = carrinhos.stream()
                    .collect(Collectors.groupingBy(carrinho -> carrinho.getCategoria().getNome()));

            for (Map.Entry<String, List<Carrinho>> entry : groupedByCategory.entrySet()) {
                Paragraph category = new Paragraph(entry.getKey(), categoryFont);
                document.add(category);

                for (Carrinho carrinho : entry.getValue()) {
                    Paragraph item = new Paragraph("  - " + carrinho.getCodigo() + ": " + carrinho.getDescricao(), itemFont);
                    document.add(item);
                }
                document.add(new Paragraph("\n"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
