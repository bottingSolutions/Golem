/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.awt.image.WritableRaster;


/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class DrawingWindow extends Window implements Runnable{

    private final Rectangle bounds;
    private BufferedImage drawingImage;
    private final WritableRaster clear;
    private boolean running;


    public DrawingWindow(Rectangle r) {
        super(null);
        this.bounds = r;
        drawingImage = new BufferedImage(bounds.width, bounds.height, TYPE_INT_ARGB);
        clear = new BufferedImage(bounds.width, bounds.height, TYPE_INT_ARGB).getRaster();
        Graphics2D g2 = drawingImage.createGraphics();
  
        final Font font = g2.getFont().deriveFont(48f);
        g2.setFont(font);

        final String message = "Hello";
        g2.setColor(Color.RED);
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(message,
                (drawingImage.getWidth() - metrics.stringWidth(message)) / 2,
                (drawingImage.getHeight() - metrics.getHeight()) / 2);
        g2.dispose();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(drawingImage, 0, 0, this);

    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public Graphics2D startDrawing() {
        this.setBackground(new Color(0, true));
        drawingImage.setData(clear);
        return drawingImage.createGraphics();

    }

    public void endDrawing() {

        this.repaint();

    }

    @Override
    public void run() {
    
       while(running){
           startDrawing();
           endDrawing();
       }
    }
}
