
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem;

import java.awt.Rectangle;
import solutions.botting.golem.script.ScriptState;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public interface GolemListener {

    public void botBoundsChanged(Rectangle bounds);
    public void botScriptStateChanged(ScriptState s);
}
