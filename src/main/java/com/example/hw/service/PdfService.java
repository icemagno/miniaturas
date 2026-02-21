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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
@Transactional(readOnly = true)
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    static {
        // Force ImageIO to scan for plugins in the classpath (important for WebP)
        javax.imageio.ImageIO.scanForPlugins();
        String[] formats = javax.imageio.ImageIO.getReaderFormatNames();
        logger.info("Formatos de imagem suportados pelo Java: {}", String.join(", ", formats));
    }

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
                        Carrinho carrinho = displayCell.getCarrinho();
                        Image image = processImage(carrinho.getImagem());
                        PdfPCell imageCell;
                        if (image != null) {
                            imageCell = new PdfPCell(image, true); // Use auto-scale constructor
                        } else {
                            imageCell = new PdfPCell();
                        }
                        imageCell.setBorder(PdfPCell.NO_BORDER);
                        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        imageCell.setPadding(2);
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

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("Relatório de Miniaturas por Categoria", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            Map<String, List<Carrinho>> groupedByCategory = carrinhos.stream()
                    .collect(Collectors.groupingBy(carrinho -> 
                        carrinho.getCategoria() != null ? carrinho.getCategoria().getNome() : "Sem Categoria"));

            for (Map.Entry<String, List<Carrinho>> entry : groupedByCategory.entrySet()) {
                Paragraph category = new Paragraph("Categoria: " + entry.getKey(), categoryFont);
                category.setSpacingBefore(10f);
                category.setSpacingAfter(5f);
                document.add(category);

                PdfPTable table = new PdfPTable(new float[]{3, 8, 3, 2});
                table.setWidthPercentage(100);

                PdfPCell h1 = new PdfPCell(new Phrase("Código", headFont));
                h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                h1.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(h1);

                PdfPCell h2 = new PdfPCell(new Phrase("Descrição", headFont));
                h2.setHorizontalAlignment(Element.ALIGN_CENTER);
                h2.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(h2);

                PdfPCell h3 = new PdfPCell(new Phrase("Expositor", headFont));
                h3.setHorizontalAlignment(Element.ALIGN_CENTER);
                h3.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(h3);

                PdfPCell h4 = new PdfPCell(new Phrase("Célula", headFont));
                h4.setHorizontalAlignment(Element.ALIGN_CENTER);
                h4.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(h4);

                for (Carrinho carrinho : entry.getValue()) {
                    PdfPCell c1 = new PdfPCell(new Phrase(carrinho.getCodigo(), itemFont));
                    table.addCell(c1);

                    PdfPCell c2 = new PdfPCell(new Phrase(carrinho.getDescricao(), itemFont));
                    table.addCell(c2);

                    String disp = (carrinho.getDisplayCell() != null && carrinho.getDisplayCell().getDisplay() != null) 
                                    ? carrinho.getDisplayCell().getDisplay().getDisplayCode() : "-";
                    PdfPCell c3 = new PdfPCell(new Phrase(disp, itemFont));
                    c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c3);

                    String cell = (carrinho.getDisplayCell() != null) ? carrinho.getDisplayCell().getCellCode() : "-";
                    PdfPCell c4 = new PdfPCell(new Phrase(cell, itemFont));
                    c4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c4);
                }
                document.add(table);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public byte[] createAlphabeticalPdf(List<Carrinho> carrinhos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("Relatório de Miniaturas - Ordem Alfabética", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(new float[]{3, 8, 3, 2});
            table.setWidthPercentage(100);

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            
            PdfPCell h1 = new PdfPCell(new Phrase("Código", headFont));
            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Descrição", headFont));
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Expositor", headFont));
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("Célula", headFont));
            h4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h4);

            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            for (Carrinho carrinho : carrinhos) {
                PdfPCell c1 = new PdfPCell(new Phrase(carrinho.getCodigo(), itemFont));
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(carrinho.getDescricao(), itemFont));
                table.addCell(c2);

                String disp = (carrinho.getDisplayCell() != null && carrinho.getDisplayCell().getDisplay() != null) 
                                ? carrinho.getDisplayCell().getDisplay().getDisplayCode() : "-";
                PdfPCell c3 = new PdfPCell(new Phrase(disp, itemFont));
                c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c3);

                String cell = (carrinho.getDisplayCell() != null) ? carrinho.getDisplayCell().getCellCode() : "-";
                PdfPCell c4 = new PdfPCell(new Phrase(cell, itemFont));
                c4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c4);
            }

            document.add(table);
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

                Image image = processImage(carrinho.getImagem());
                if (image != null) {
                    image.scaleToFit(120, 120);
                    cell.addElement(image);
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

    public byte[] createUncheckedPdf(List<Carrinho> carrinhos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("Relatório de Miniaturas NÃO CHECADAS (Ordem Alfabética)", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(new float[]{3, 8, 4});
            table.setWidthPercentage(100);

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            
            PdfPCell h1 = new PdfPCell(new Phrase("Código", headFont));
            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            h1.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Descrição", headFont));
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Categoria", headFont));
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);
            h3.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(h3);

            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // Sort alphabetical by description
            carrinhos.sort((c1, c2) -> c1.getDescricao().compareToIgnoreCase(c2.getDescricao()));

            for (Carrinho carrinho : carrinhos) {
                PdfPCell c1 = new PdfPCell(new Phrase(carrinho.getCodigo(), itemFont));
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(carrinho.getDescricao(), itemFont));
                table.addCell(c2);

                String cat = (carrinho.getCategoria() != null) ? carrinho.getCategoria().getNome() : "-";
                PdfPCell c3 = new PdfPCell(new Phrase(cat, itemFont));
                table.addCell(c3);
            }

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private Image processImage(String base64Image) {
        if (base64Image == null || base64Image.trim().isEmpty() || base64Image.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            String data = base64Image.trim();
            if (data.contains(",")) {
                data = data.substring(data.indexOf(",") + 1);
            }
            
            // Standardize URL-safe base64 if present
            data = data.replace('-', '+').replace('_', '/');
            
            // MimeDecoder is very robust: it automatically skips any non-base64 characters 
            // like newlines or spaces without needing a risky regex.
            byte[] imageBytes = Base64.getMimeDecoder().decode(data);
            
            if (imageBytes.length == 0) return null;

            // Check if it's WebP (RIFF header: 52 49 46 46)
            boolean isWebP = imageBytes.length > 4 && 
                             imageBytes[0] == 0x52 && imageBytes[1] == 0x49 && 
                             imageBytes[2] == 0x46 && imageBytes[3] == 0x46;

            if (isWebP) {
                // For WebP, always go through ImageIO conversion to be safe
                try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                    BufferedImage bimg = ImageIO.read(bais);
                    if (bimg != null) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            ImageIO.write(bimg, "png", baos);
                            return Image.getInstance(baos.toByteArray());
                        }
                    }
                }
            }

            try {
                // Try iText's native reader first (works for PNG/JPG)
                return Image.getInstance(imageBytes);
            } catch (Exception itextEx) {
                // Fallback: try to "repair" or convert the image using ImageIO
                try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                    BufferedImage bimg = ImageIO.read(bais);
                    if (bimg != null) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            ImageIO.write(bimg, "png", baos);
                            return Image.getInstance(baos.toByteArray());
                        }
                    } else {
                        // Diagnostic: What are we actually looking at?
                        StringBuilder magic = new StringBuilder();
                        for (int i = 0; i < Math.min(imageBytes.length, 4); i++) {
                            magic.append(String.format("%02X", imageBytes[i]));
                        }
                        logger.error("Java não reconheceu o formato da imagem. Magic Bytes: {} (Tamanho: {})", 
                                     magic.toString(), imageBytes.length);
                    }
                }
                return null;
            }
        } catch (Exception e) {
            logger.error("Erro ao decodificar Base64: {}", e.getMessage());
            return null;
        }
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
