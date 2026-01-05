package com.example.hw.service;

import de.androidpit.colorthief.ColorThief;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorService {

    public List<int[]> extractPalette(String base64Image, int numColors) throws IOException {
        if (base64Image == null || base64Image.isEmpty() || !base64Image.contains(",")) {
            return null;
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bis);

        if (image == null) {
            // ImageIO.read returns null if the format is not supported or image is corrupt
            return null;
        }

        // ColorThief.getPalette(image, numColors) returns an array of int[3] (r,g,b)
        int[][] palette = ColorThief.getPalette(image, numColors);
        if (palette == null) {
            return null;
        }
        return Arrays.stream(palette).collect(Collectors.toList());
    }

    public BufferedImage createPaletteImage(List<int[]> palette, int width, int height) {
        if (palette == null || palette.isEmpty()) {
            return null;
        }

        BufferedImage paletteImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = paletteImage.createGraphics();

        int swatchWidth = width / palette.size();
        for (int i = 0; i < palette.size(); i++) {
            int[] rgb = palette.get(i);
            g2d.setColor(new Color(rgb[0], rgb[1], rgb[2]));
            g2d.fillRect(i * swatchWidth, 0, swatchWidth, height);
        }

        g2d.dispose();
        return paletteImage;
    }
}
