/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.gui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import solutions.botting.golem.color.ColorPoint;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class ColorPickerTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"X", "Y", "Color", "Red", "Green", "Blue"};
    private final ArrayList<ColorPoint> colorList;

    public ColorPickerTableModel() {
        this.colorList = new ArrayList<ColorPoint>();
    }

    @Override
    public int getRowCount() {
        return this.colorList.size();
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ColorPoint cp = this.colorList.get(rowIndex);
        Color c = cp.getColor();
        switch (columnIndex) {
            case 0:
                return cp.getX();

            case 1:
                return cp.getY();

            case 2:
                return c.getRGB();
            case 3:
                return c.getRed();
            case 4:
                return c.getGreen();
            case 5:
                return c.getBlue();

        }
        return null;
    }

    public void addRow(ColorPoint colorPoint) {
        this.colorList.add(colorPoint);
           fireTableRowsInserted(this.colorList.size(), this.colorList.size());
    }

}
