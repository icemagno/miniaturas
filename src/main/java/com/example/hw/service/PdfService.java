package com.example.hw.service;

import com.example.hw.model.Carrinho;
import com.example.hw.model.Display;
import com.example.hw.model.DisplayCell;
import com.example.hw.repository.DisplayRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Base64;


@Service
public class PdfService {

    @Autowired
    private DisplayRepository displayRepository;

    public byte[] createDisplayPdf(Long displayId) {
        Optional<Display> displayOptional = displayRepository.findById(displayId);
        if (displayOptional.isEmpty()) {
            return new byte[0];
        }
        Display display = displayOptional.get();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16);
            document.add(new Paragraph("Expositor: " + display.getDisplayCode(), titleFont));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            Map<String, DisplayCell> cellMap = display.getCells().stream()
                    .collect(Collectors.toMap(DisplayCell::getCellCode, Function.identity()));

            char[] rows = "ABCDEFGHIJKLMNO".toCharArray();
            for (char row : rows) {
                for (int col = 1; col <= 5; col++) {
                    String cellCode = row + String.valueOf(col);
                    DisplayCell displayCell = cellMap.get(cellCode);

                    PdfPCell cell = new PdfPCell();
                    cell.setFixedHeight(45f);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

                    if (displayCell != null && displayCell.getCarrinho() != null) {
                        Font mainFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
                        cell.setPhrase(new Phrase(displayCell.getCarrinho().getCodigo(), mainFont));
                        cell.setBackgroundColor(new Color(223, 240, 216)); // #dff0d8
                    } else {
                        Font placeholderFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.LIGHT_GRAY);
                        cell.setPhrase(new Phrase(cellCode, placeholderFont));
                    }
                    table.addCell(cell);
                }
            }
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

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

    public byte[] createPdfWithPictures(List<Carrinho> carrinhos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (Carrinho carrinho : carrinhos) {
                PdfPCell cell = new PdfPCell();
                cell.setPadding(5);

                if (carrinho.getImagem() != null && !carrinho.getImagem().isEmpty()) {
                    try {
                        byte[] imageBytes = Base64.getDecoder().decode(carrinho.getImagem().split(",")[1]);
                        Image image = Image.getInstance(imageBytes);
                        image.scaleToFit(120, 120);
                        cell.addElement(image);
                    } catch (IOException | DocumentException e) {
                        cell.addElement(createPlaceholderCell());
                    }
                } else {
                    cell.addElement(createPlaceholderCell());
                }

                Paragraph details = new Paragraph(carrinho.getCodigo() + "\n" + carrinho.getDescricao(), itemFont);
                cell.addElement(details);
                table.addCell(cell);
            }

            // Fill remaining cells in the last row
            int remaining = 4 - (carrinhos.size() % 4);
            if (remaining > 0 && remaining < 4) {
                for (int i = 0; i < remaining; i++) {
                    table.addCell(new PdfPCell(new Phrase("")));
                }
            }

            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private PdfPTable createPlaceholderCell() {
        PdfPTable placeholder = new PdfPTable(1);
        placeholder.setWidthPercentage(100);
        PdfPCell innerCell = new PdfPCell(new Phrase("Sem Imagem"));
        innerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        innerCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        innerCell.setFixedHeight(120);
        placeholder.addCell(innerCell);
        return placeholder;
    }
}
