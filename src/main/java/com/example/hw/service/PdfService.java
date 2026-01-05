package com.example.hw.service;

import com.example.hw.model.Carrinho;
import com.example.hw.model.Display;
import com.example.hw.model.DisplayCell;
import com.example.hw.repository.DisplayRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;


@Service
public class PdfService {

    @Autowired
    private DisplayRepository displayRepository;

    @Autowired
    private ColorService colorService;

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

            Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, Color.BLACK);
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
                    cell.setFixedHeight(30f);
                    cell.setPadding(0); // Remove padding to let nested table fill the space

                    if (displayCell != null && displayCell.getCarrinho() != null) {
                        // Create a nested table for image and text
                        PdfPTable nestedTable = new PdfPTable(2);
                        nestedTable.setWidthPercentage(100);
                        nestedTable.setWidths(new float[]{1.5f, 2f}); // Adjust column widths

                        // Image Cell (Left)
                        PdfPCell imageCell = new PdfPCell();
                        imageCell.setBorder(PdfPCell.NO_BORDER);
                        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                        Carrinho carrinho = displayCell.getCarrinho();
                        if (carrinho.getImagem() != null) {
                            try {
                                byte[] imageBytes = Base64.getDecoder().decode(carrinho.getImagem().split(",")[1]);
                                Image image = Image.getInstance(imageBytes);
                                image.scaleToFit(50, 25); // Scale to the desired size
                                imageCell.setImage(image);
                            } catch (IOException | DocumentException e) {
                                e.printStackTrace(); // fall through, imageCell will be empty
                            }
                        }
                        nestedTable.addCell(imageCell);

                        // Text Cell (Right)
                        Font mainFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
                        PdfPCell textCell = new PdfPCell(new Phrase(carrinho.getCodigo(), mainFont));
                        textCell.setBorder(PdfPCell.NO_BORDER);
                        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        nestedTable.addCell(textCell);

                        cell.addElement(nestedTable);

                    } else {
                        // Empty cell logic
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        Font placeholderFont = FontFactory.getFont(FontFactory.HELVETICA, 6, Color.BLACK);
                        cell.setPhrase(new Phrase(cellCode, placeholderFont));
                    }
                    table.addCell(cell);
                }
            }
            document.add(table);

        } catch (Exception e) {
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
