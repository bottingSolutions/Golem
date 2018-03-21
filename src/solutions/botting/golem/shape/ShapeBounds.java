/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.shape;

import java.awt.Rectangle;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class ShapeBounds extends Rectangle{
    private final ShapeType type;
    private final Rectangle bounds;

    public ShapeBounds(ShapeType type, Rectangle bounds) {
        this.type = type;
        this.bounds = bounds;
    }

    public ShapeType getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    
}
