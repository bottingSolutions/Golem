/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.api;

import java.awt.Color;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import solutions.botting.golem.Golem;
import solutions.botting.golem.color.ColorPoint;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
//todo rewrite all of this code.
public class ColorApi extends GolemApi {

    public ColorApi(Golem golem) {
        super(golem);
    }

    public ColorPoint findColor(int color) {
        Rectangle bounds = golem.getBotBounds();
        return findColor(color, bounds);
    }

    public List<ColorPoint> findColors(int color) {
        Rectangle bounds = golem.getBotBounds();
        return findColorPoints(color, bounds);
    }

    public ColorPoint findColor(int color, Rectangle bounds) {
        BufferedImage image = golem.getRobot().createScreenCapture(bounds);
        for (int x = 0; x < bounds.getWidth(); x++) {
            for (int y = 0; y < bounds.getHeight(); y++) {
                int c = image.getRGB(x, y);
                if (c == color) {
                    return new ColorPoint(new Color(color), x, y);
                }
            }
        }
        return null;
    }

    public ColorPoint findColorInArea(int color, int x1, int y1, int x2, int y2) {

        return findColorInArea(color, x1, y1, x2, y2, true, true, true);
    }

    public ColorPoint findColorInArea(int color, int x1, int y1, int x2, int y2, boolean rightToLeft, boolean topToBottom, boolean xPriority) {
        Rectangle bounds = golem.getBotBounds();
        BufferedImage image = golem.getRobot().createScreenCapture(bounds);
        return findColorInAreaInImage(color, x1, y1, x2, y2, rightToLeft, topToBottom, xPriority, image);
    }

    public ColorPoint findColorInAreaInImage(int color, int x1, int y1, int x2, int y2, boolean leftToRight, boolean topToBottom, boolean xPriority, BufferedImage image) {
        int xStart = leftToRight ? x1 : 0 - x2;
        int xLimit = leftToRight ? x2 : 0 - x1;
        int yStart = topToBottom ? y1 : 0 - y2;
        int yLimit = topToBottom ? y2 : 0 - y1;
        if (xPriority) {
            for (int x = xStart; x < xLimit; x++) {
                for (int y = yStart; y < yLimit; y++) {
                    int c = image.getRGB(leftToRight ? x : Math.abs(x), topToBottom ? y : Math.abs(y));
                    if (c == color) {
                        return new ColorPoint(new Color(color), leftToRight ? x : Math.abs(x), topToBottom ? y : Math.abs(y));
                    }
                }
            }
        } else {

            for (int y = yStart; y < yLimit; y++) {
                for (int x = xStart; x < xLimit; x++) {
                    int c = image.getRGB(leftToRight ? x : Math.abs(x), topToBottom ? y : Math.abs(y));
                    if (c == color) {
                        return new ColorPoint(new Color(color), leftToRight ? x : Math.abs(x), topToBottom ? y : Math.abs(y));
                    }
                }
            }

        }
        return null;
    }

    public boolean isColorAtPoint(int x, int y, int color) {
        Rectangle bounds = golem.getBotBounds();
        BufferedImage image = golem.getRobot().createScreenCapture(bounds);
        return color == image.getRGB(x, y);
    }

    public boolean isColorsAtPoints(List<ColorPoint> points) {
        Rectangle bounds = golem.getBotBounds();
        return isColorAtPointsInImage(points, golem.getRobot().createScreenCapture(bounds));
    }

    public boolean isColorAtPointsInImage(List<ColorPoint> points, BufferedImage image) {

        for (ColorPoint cp : points) {
            Double x = cp.getX();
            Double y = cp.getY();
            if (cp.getColor().getRGB() != image.getRGB(x.intValue(), y.intValue())) {
                Color c = new Color(image.getRGB(x.intValue(), y.intValue()));
                return false;
            }
        }
        return true;
    }

    private List<ColorPoint> findColorPoints(int color, Rectangle bounds) {
        return findColorPointsInImage(color, super.getBufferedImageOfBotBounds());
    }

    public List findColorPointsInImage(int color, BufferedImage image) {

        ArrayList<ColorPoint> points = new ArrayList<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int c = image.getRGB(x, y);
                if (c == color) {
                    points.add(new ColorPoint(new Color(color), x, y));
                }
            }

        }
        return points;
    }

    public List<ColorPoint> getColors() {
        Rectangle bounds = golem.getBotBounds();
        return getColors(bounds);
    }

    private List<ColorPoint> getColors(Rectangle bounds) {
        return getColorsInImage(super.getBufferedImageOfBotBounds());
    }

    public List<ColorPoint> getColorsInImage(BufferedImage image) {
        ArrayList<ColorPoint> points = new ArrayList<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int c = image.getRGB(x, y);

                points.add(new ColorPoint(new Color(c), x, y));

            }
        }
        return points;
    }

    public int countColors(int color) {
        Rectangle bounds = golem.getBotBounds();
        return countColors(color, bounds);
    }

    public int countColorOccurancesInImage(int color, BufferedImage image) {
        int i = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int c = image.getRGB(x, y);
                if (c == color) {
                    i++;
                }
            }
        }
        return i;
    }

    private int countColors(int color, Rectangle bounds) {
        return countColorOccurancesInImage(color, super.getBufferedImageOfBotBounds());
    }

}
