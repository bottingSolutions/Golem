/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import solutions.botting.golem.api.Api;
import solutions.botting.golem.api.ColorApi;
import solutions.botting.golem.api.ShapeApi;
import solutions.botting.golem.core.RobotHelper;
import static solutions.botting.golem.script.ScriptState.*;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
class ScriptRunner extends Thread implements Runnable {

    private String script;
    private File scriptFile;
    private Golem golem;
    private RobotHelper robotHelper;

    ScriptRunner(String s, Golem aThis, RobotHelper robotHelper) {
        this.golem = aThis;
        this.robotHelper = robotHelper;
        this.script = s;

    }

    ScriptRunner(File s, Golem aThis, RobotHelper robotHelper) {
        this.golem = aThis;
        this.robotHelper = robotHelper;
        this.scriptFile = s;

    }

    public void run() {
        try {
            // create a script engine manager
            ScriptEngineManager factory = new ScriptEngineManager();

            ScriptEngine engine = factory.getEngineByName("Nashorn");
            engine.put("input", robotHelper);

            engine.put("golem", golem);
            engine.put("color", new ColorApi(golem));
            engine.put("shape", new ShapeApi(golem));
            engine.put("api", new Api(golem));
            if (script != null) {
                engine.eval(script);
                return;
            } else {
                engine.eval(new FileReader(scriptFile));
                return;
            }

        } catch (FileNotFoundException | ScriptException ex) {
            System.out.println(ex);
            golem.fireScriptStateChange(EXCEPTION);
        }
        golem.fireScriptStateChange(ENDED);

    }

    @Override
    public void interrupt() {
        golem.fireScriptStateChange(INTERRUPT);
        super.interrupt();
    }

    @Override
    public synchronized void start() {
        golem.fireScriptStateChange(RUNNING);
        super.start(); //To change body of generated methods, choose Tools | Templates.

    }

}
