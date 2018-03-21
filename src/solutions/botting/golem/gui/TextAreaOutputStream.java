/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.gui;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;


public class TextAreaOutputStream extends OutputStream {

    public static final int DEFAULT_BUFFER_SIZE = 1;

    JTextArea mText;
    byte mBuf[];
    int mLocation;

    public TextAreaOutputStream(JTextArea component) {
        this(component, DEFAULT_BUFFER_SIZE);
        DefaultCaret caret = (DefaultCaret) component.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public TextAreaOutputStream(JTextArea component, int bufferSize) {
        mText = component;
        if (bufferSize < 1) {
            bufferSize = 1;
        }
        mBuf = new byte[bufferSize];
        mLocation = 0;
    }

    @Override
    public void write(int arg0) throws IOException {
        //System.err.println("arg = "  + (char) arg0);
        mBuf[mLocation++] = (byte) arg0;
        if (mLocation == mBuf.length) {
            flush();
        }
    }

    public void flush() {
        mText.append(new String(mBuf, 0, mLocation));

        mLocation = 0;
    }

}
