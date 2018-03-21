/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.ocr;

import java.awt.Rectangle;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class WordBounds extends Rectangle {

    private final String word;

    public WordBounds(String word, Rectangle r) {

        super(r);
        this.word = word;
    }

    public String getWord() {
        return word;
    }

}
