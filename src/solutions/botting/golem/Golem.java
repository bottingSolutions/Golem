/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;
import solutions.botting.golem.plugin.PluginLibrary;
import solutions.botting.golem.core.RobotHelper;
import solutions.botting.golem.plugin.DefaultExternalResourceFetcher;
import solutions.botting.golem.plugin.ExternalResourceFetcher;
import solutions.botting.golem.plugin.GolemResourceUpdateException;
import solutions.botting.golem.plugin.PluginResource;
import solutions.botting.golem.script.ScriptState;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class Golem {

    private Rectangle botBounds;

    private final boolean running;
    private final Toolkit toolkit;
    private final Robot robot;
    private Thread currentScriptThread;
    private final ArrayList<GolemListener> listeners;
    private String pluginDirectory;

    public Golem() throws AWTException, InterruptedException {
        this.running = true;
        this.toolkit = Toolkit.getDefaultToolkit();
        this.robot = new Robot();
        this.botBounds = new Rectangle(toolkit.getScreenSize());
        this.listeners = new ArrayList<>();
        this.pluginDirectory = DEFAULT_PLUGIN_DIRECTORY;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.loadLibrary("lib/x64/opencv_java340");

        try {
            Golem bot = new Golem();
            if (args.length != 0 && args[0] != null) {
                bot.runScript(new File(args[0]));
            } else {
                java.awt.EventQueue.invokeLater(() -> {
                    GolemFrame gf = new GolemFrame(bot);

                    gf.setVisible(true);
                    System.setOut(gf.getOutputStream());
                });

            }

        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void runScript(String s) {
        Runnable r = new ScriptRunner(s, this, new RobotHelper(this, robot));
        currentScriptThread = (Thread) r;
        currentScriptThread.start();
    }

    public void runScript(File f) {
        Runnable r = new ScriptRunner(f, this, new RobotHelper(this, robot));
        currentScriptThread = (Thread) r;
        currentScriptThread.start();
    }

    public Toolkit getToolkit() {
        return toolkit;
    }

    public Rectangle getBotBounds() {
        return botBounds;
    }

    public void setBotBoundsFullScreen() {
        setBotBounds(new Rectangle(toolkit.getScreenSize()));
    }

    public Robot getRobot() {
        return robot;
    }

    public void setBotBounds(Rectangle botBounds) {
        this.botBounds = botBounds;
        this.fireBotBoundsChange(botBounds);
    }

    public Thread getCurrentScript() {

        return currentScriptThread;

    }

    public synchronized void addListener(GolemListener l) {
        this.listeners.add(l);
    }

    public synchronized void removeListener(GolemListener l) {
        this.listeners.remove(l);
    }

    public synchronized void fireBotBoundsChange(Rectangle r) {
        listeners.forEach((l) -> {
            l.botBoundsChanged(r);
        });
    }

    public synchronized void fireScriptStateChange(ScriptState s) {
        listeners.forEach((l) -> {
            l.botScriptStateChanged(s);
        });
    }

    public void sleep(long l) throws InterruptedException {
        getCurrentScript().sleep(l);
    }

    public void loadDirectory(String directory) throws MalformedURLException {
        File loc = new File(directory);
        File[] flist = loc.listFiles(new FileFilter() {

            public boolean accept(File file) {
                return file.getPath().toLowerCase().endsWith(".jar");
            }
        });
        URL[] urls = new URL[flist.length];
        for (int i = 0; i < flist.length; i++) {
            urls[i] = flist[i].toURI().toURL();
            System.out.println(urls[i]);
        }
        URLClassLoader ucl = new URLClassLoader(urls);

        ServiceLoader<PluginLibrary> sl = ServiceLoader.load(PluginLibrary.class, ucl);
        Iterator<PluginLibrary> apit = sl.iterator();
        System.out.println(apit.hasNext());
        while (apit.hasNext()) {
            System.out.println(apit.next().getName());
        }

    }

    public Object loadPlugin(PluginResource pr) throws GolemResourceUpdateException {
        DefaultExternalResourceFetcher derf = new DefaultExternalResourceFetcher(this.getPluginDirectory());
        return loadPlugin(pr, derf);
    }

    public Object loadPlugin(PluginResource pr, ExternalResourceFetcher f) throws GolemResourceUpdateException {
        return f.loadExternalResource(pr);
    }

    public void loadClasses(URL[] url) {

    }

    public String getPluginDirectory() {
        return pluginDirectory;
    }

    public void setPluginDirectory(String s) {
        pluginDirectory = s;
    }
    public static final String DEFAULT_PLUGIN_DIRECTORY = "plugins";
}
