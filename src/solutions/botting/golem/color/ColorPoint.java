/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.color;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class ColorPoint extends Point {

    private Color color;

    public ColorPoint(Color color, Point p) {
        super(p);
        this.color = color;
    }

    public ColorPoint(Color color, int x, int y) {
        super(x, y);
        this.color = color;
    }

    public ColorPoint(int color, int x, int y) {
        super(x, y);
        this.color = new Color(color);
    }

    public ColorPoint(int r, int g, int b, int x, int y) {
        super(x, y);
        this.color = new Color(r, g, b);
    }

    public Color getColor() {
        return color;
    }

}
