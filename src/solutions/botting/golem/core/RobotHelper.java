/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.core;

/**
 *
 * @author Silabsoft <admin@silabsoft.org>
 */
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import solutions.botting.golem.Golem;

/**
 * This is a modified version of the EventNazi.class from SMART
 *
 * @author Benjamin J. Land, unsignedByte
 */
public class RobotHelper {

    private int cx, cy;
    private final Set<int[]> keysHeld;
    private boolean leftDown;
    private boolean midDown;
    private boolean rightDown;
    private boolean shiftDown;
    private final Robot robot;
    private final Golem golem;

    public RobotHelper(Golem golem, Robot robot) {
        this.robot = robot;
        this.golem = golem;
        leftDown = false;
        rightDown = false;

        shiftDown = false;
        keysHeld = Collections.synchronizedSet(new HashSet<int[]>());

    }

    public void wait(int min, int max) {
        int time = min == max ? min : (int) ((Math.random() * Math.abs(max - min)) + Math.min(min, max));
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wait(int mills) {
        wait(mills, mills);
    }

    public Point getMousePos() {
        return new Point(cx, cy);
    }

    private synchronized Point moveMouseImpl(int x, int y) {
        Rectangle bounds = golem.getBotBounds();
        Double bx = bounds.getX();
        Double by = bounds.getY();
        robot.mouseMove(x + bx.intValue(), y + by.intValue());
        cx = x;
        cy = y;
        return new Point(cx, cy);
    }

    /**
     * Internal mouse movement algorithm. Do not use this without credit to
     * either Benjamin J. Land or BenLand100. This is synchronized to prevent
     * multiple motions and bannage.
     *
     * @param xs The x start
     * @param ys The y start
     * @param xe The x destination
     * @param ye The y destination
     * @param gravity Strength pulling the position towards the destination
     * @param wind Strength pulling the position in random directions
     * @param minWait Minimum relative time per step
     * @param maxWait Maximum relative time per step
     * @param maxStep Maximum size of a step, prevents out of control motion
     * @param targetArea Radius of area around the destination that should
     * trigger slowing, prevents spiraling
     * @result The actual end point
     */
    private synchronized Point windMouseImpl(double xs, double ys, double xe, double ye, double gravity, double wind, double minWait, double maxWait, double maxStep, double targetArea) {
        //System.out.println(targetArea);
        final double sqrt3 = Math.sqrt(3);
        final double sqrt5 = Math.sqrt(5);

        double dist, veloX = 0, veloY = 0, windX = 0, windY = 0;
        while ((dist = Math.hypot(xs - xe, ys - ye)) >= 1) {
            wind = Math.min(wind, dist);
            if (dist >= targetArea) {
                windX = windX / sqrt3 + (2D * Math.random() - 1D) * wind / sqrt5;
                windY = windY / sqrt3 + (2D * Math.random() - 1D) * wind / sqrt5;
            } else {
                windX /= sqrt3;
                windY /= sqrt3;
                if (maxStep < 3) {
                    maxStep = Math.random() * 3D + 3D;
                } else {
                    maxStep /= sqrt5;
                }
                //System.out.println(maxStep + ":" + windX + ";" + windY);
            }
            veloX += windX + gravity * (xe - xs) / dist;
            veloY += windY + gravity * (ye - ys) / dist;
            double veloMag = Math.hypot(veloX, veloY);
            if (veloMag > maxStep) {
                double randomDist = maxStep / 2D + Math.random() * maxStep / 2D;
                veloX = (veloX / veloMag) * randomDist;
                veloY = (veloY / veloMag) * randomDist;
            }
            xs += veloX;
            ys += veloY;
            int mx = (int) Math.round(xs);
            int my = (int) Math.round(ys);
            if (cx != mx || cy != my) {
                //Scratch
                /*g.drawLine(cx,cy,mx,my);
                frame.repaint();*/
                //MouseJacking
                /*try {
                    Robot r = new Robot();
                    r.mouseMove(mx,my);
                } catch (Exception e) { } */
                moveMouseImpl(mx, my);
            }
            double step = Math.hypot(xs - cx, ys - cy);
            try {
                Thread.sleep(Math.round((maxWait - minWait) * (step / maxStep) + minWait));
            } catch (InterruptedException ex) {
            }
        }
        //System.out.println(Math.abs(xe - cx) + ", " + Math.abs(ye - cy));
        return new Point(cx, cy);
    }

    /**
     * Moves the mouse from the current position to the specified position.
     * Approximates human movement in a way where smoothness and accuracy are
     * relative to speed, as it should be.
     *
     * @param x The x destination
     * @param y The y destination
     * @result The actual end point
     */
    public synchronized Point windMouse(int x, int y) {

        double speed = (Math.random() * 15D + 15D) / 10D;
        return windMouseImpl(cx, cy, x, y, 9D, 3D, 5D / speed, 10D / speed, 10D * speed, 8D * speed);

    }

    /**
     * Moves the mouse from the current position to the specified position.
     * Approximates human movement in a way where smoothness and accuracy are
     * relative to speed, as it should be.
     *
     * @param x The x destination
     * @param y The y destination
     * @result The actual end point
     */
    public synchronized Point moveMouse(int x, int y) {

        return moveMouseImpl(x, y);

    }

    /**
     * Holds the mouse at the specified position after moving from the current
     * position to the specified position.
     *
     * @param x The x destination
     * @param y The y destination
     * @result The actual end point
     */
    public synchronized Point holdMouse(int x, int y, int button) {
        if (canHold(button)) {
            int btnMask = ((leftDown || button == 1) ? MouseEvent.BUTTON1_DOWN_MASK : 0) | ((midDown || button == 2) ? (MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.META_DOWN_MASK) : 0) | ((rightDown || button == 3) ? (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.META_DOWN_MASK) : 0);
            int btn = 0;
            switch (button) {
                case 1:
                    btn = MouseEvent.BUTTON1;
                    break;
                case 2:
                    btn = MouseEvent.BUTTON2;
                    break;
                case 3:
                    btn = MouseEvent.BUTTON3;
                    break;
            }
            Point end = moveMouse(x, y);

            robot.mousePress(btnMask);
            switch (button) {
                case 1:
                    leftDown = true;
                    break;
                case 2:
                    midDown = true;
                    break;
                case 3:
                    rightDown = true;
                    break;
            }

            return end;
        }
        return null;
    }

    /**
     * Releases the mouse at the specified position after moving from the
     * current position to the specified position.
     *
     * @param x The x destination
     * @param y The y destination
     * @result The actual end point
     */
    public synchronized Point releaseMouse(int x, int y, int button) {
        if (canRelease(button)) {
            int btnMask = ((leftDown || button == 1) ? MouseEvent.BUTTON1_DOWN_MASK : 0) | ((midDown || button == 2) ? (MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.META_DOWN_MASK) : 0) | ((rightDown || button == 3) ? (MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.META_DOWN_MASK) : 0);
            int btn = 0;
            switch (button) {
                case 1:
                    btn = MouseEvent.BUTTON1;
                    break;
                case 2:
                    btn = MouseEvent.BUTTON2;
                    break;
                case 3:
                    btn = MouseEvent.BUTTON3;
                    break;
            }
            Point end = moveMouse(x, y);

            robot.mouseRelease(btnMask);
            switch (button) {
                case 1:
                    leftDown = false;
                    break;
                case 2:
                    midDown = false;
                    break;
                case 3:
                    rightDown = false;
                    break;
            }

            return end;
        }
        return null;
    }

    /**
     * Clicks the mouse at the specified position after moving from the current
     * position to the specified position.
     *
     * @param x The x destination
     * @param y The y destination
     * @result The actual end point
     */
    public synchronized Point clickMouse(int x, int y, int button) {

        Point end = moveMouse(x, y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        System.out.println("hey");
        return end;

    }

    public synchronized boolean isMouseButtonHeld(int button) {
        switch (button) {
            case 1:
                return leftDown;
            case 2:
                return midDown;
            case 3:
                return rightDown;
        }
        return false;
    }

    public synchronized Point scrollMouse(int x, int y, int lines) {
        int btnMask = (isKeyDown(KeyEvent.VK_SHIFT) ? KeyEvent.SHIFT_MASK : 0) | (isKeyDown(KeyEvent.VK_ALT) ? KeyEvent.ALT_MASK : 0) | (isKeyDown(KeyEvent.VK_CONTROL) ? KeyEvent.CTRL_MASK : 0);

        Point end = moveMouse(x, y);
        robot.mouseWheel(Math.abs(lines));

        return new Point(x, y);

    }

    /**
     * Tests if a character requires the shift key to be pressed.
     *
     * @param c Char to check for
     * @result True if shift is required
     */
    private boolean isShiftChar(char c) {
        String special = "~!@#$%^&*()_+|{}:\"<>?";
        return special.indexOf(c) != -1 || (c - 'A' >= 0 && c - 'A' <= 25);
    }

    /**
     * Holds a key. Should be used for any key that needs to be held, not
     * sending text.
     *
     * @param code KeyCode for the key
     */
    public synchronized void holdKey(int code) {

        long startTime = System.currentTimeMillis();
        int[] dat = new int[]{code, (int) (startTime & 0xFFFFFFFF)};
        if (!isKeyHeld(dat)) {
            if (KeyEvent.VK_SHIFT == code) {
                shiftDown = true;
            }
            robot.keyPress(code);

            setKeyHeld(dat, true);
        }

    }

    /**
     * Release a key. Should be used for any key that needs to be held, not
     * sending text. Will only release it if its already held.
     *
     * @param code KeyCode for the key
     */
    public synchronized void releaseKey(int code) {

        long startTime = System.currentTimeMillis();
        int[] dat = new int[]{code};
        if (isKeyHeld(dat)) {
            setKeyHeld(dat, false);
            robot.keyRelease(code);
            if (KeyEvent.VK_SHIFT == code) {
                shiftDown = false;
            }

        }
    }
    public static int[] typable_vk_keycode = new int[0xff];

    static {
        typable_vk_keycode[32] = 32;
        for (int c = (int) 'A'; c <= (int) 'Z'; c++) {
            typable_vk_keycode[c] = c;
        }
        for (int c = (int) '0'; c <= (int) '9'; c++) {
            typable_vk_keycode[c] = c;
        }
        typable_vk_keycode[186] = ';'; //  ;:
        typable_vk_keycode[187] = '='; //  =+
        typable_vk_keycode[188] = ','; // hack: ,
        typable_vk_keycode[189] = '-'; //  -_
        typable_vk_keycode[190] = '.'; //  .>
        typable_vk_keycode[191] = '/'; //  /?
        typable_vk_keycode[192] = '`'; //  `~
        typable_vk_keycode[219] = '['; //  [{
        typable_vk_keycode[220] = '\\';//  \|
        typable_vk_keycode[221] = ']'; //  ]}
        typable_vk_keycode[222] = '\'';//  '"
        typable_vk_keycode[226] = ','; // hack: <
    }

    /**
     * Converts a char into a KeyCode value for KeyEvent
     *
     * @param c Char to convert
     * @result c's KeyCode
     */
    private int toKeyCode(char c) {
        final String special = "~!@#$%^&*()_+|{}:\"<>?";
        final String normal = "`1234567890-=\\[];',./";
        int index = special.indexOf(c);
        return Character.toUpperCase(index == -1 ? c : normal.charAt(index));
    }

    /**
     * Converts a vk code into a char
     *
     * @param code KeyCode to convert
     * @result the char
     */
    private char toChar(int vk, boolean shift) {
        int code = typable_vk_keycode[vk];
        final String special = "~!@#$%^&*()_+|{}:\"<>?";
        final String normal = "`1234567890-=\\[];',./";
        int index = normal.indexOf((char) code);
        if (index == -1) {
            return shift ? Character.toUpperCase((char) code) : Character.toLowerCase((char) code);
        } else {
            return shift ? special.charAt(index) : (char) code;
        }
    }

    public boolean isKeyDown(int code) {
        int[] dat = new int[]{code};
        return isKeyHeld(dat);
    }

    /**
     * Sends a string to the client like a person would type it. In Scar you can
     * use Chr(10) for enter, not Chr(13) Not to be used for arrow keys, but can
     * be used with F keys or the like
     *
     * @param text String to send to the client
     */
    public synchronized void sendKeys(String text, int keywait, int keymodwait) {

        char[] chars = text.toCharArray();

        for (char c : chars) {

            int code = toKeyCode(c);
            int keyLoc = Character.isDigit(c) ? Math.random() > 0.5D ? KeyEvent.KEY_LOCATION_NUMPAD : KeyEvent.KEY_LOCATION_STANDARD : KeyEvent.KEY_LOCATION_STANDARD;
            if (isShiftChar(c)) {
                int shiftLoc = Math.random() > 0.5D ? KeyEvent.KEY_LOCATION_RIGHT : KeyEvent.KEY_LOCATION_LEFT;
                robot.keyPress(KeyEvent.VK_SHIFT);

                try {
                    Thread.sleep((int) ((Math.random() * 0.1 + 1) * keymodwait));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long time = System.currentTimeMillis();
                robot.keyPress(code);

                try {
                    Thread.sleep((int) ((Math.random() * 0.1 + 1) * keywait));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                robot.keyRelease(code);
                try {
                    Thread.sleep((int) ((Math.random() * 0.1 + 1) * keymodwait));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                robot.keyRelease(KeyEvent.VK_SHIFT);
            } else {
                long time = System.currentTimeMillis();
                robot.keyPress(code);
                try {
                    Thread.sleep((int) ((Math.random() * 0.1 + 1) * keywait));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                robot.keyRelease(code);
            }
        }

    }

    private synchronized void setKeyHeld(int[] dat, boolean held) {
        synchronized (keysHeld) {
            if (held) {
                keysHeld.add(dat);
            } else {
                HashSet<int[]> remove = new HashSet<int[]>();
                for (int[] entry : keysHeld) {
                    if (entry[0] == dat[0]) {
                        remove.add(entry);
                    }
                }
                keysHeld.removeAll(remove);
            }
        }
    }

    private synchronized boolean isKeyHeld(int[] dat) {
        synchronized (keysHeld) {
            for (int[] entry : keysHeld) {
                if (entry[0] == dat[0]) {
                    return true;
                }
            }
            return false;
        }
    }

    public synchronized boolean isDragging() {
        return leftDown || midDown || rightDown;
    }

    public synchronized boolean isDown(int button) {
        switch (button) {
            case 1:
                return leftDown;
            case 2:
                return midDown;
            case 3:
                return rightDown;
        }
        return false;
    }

    public synchronized boolean canClick(int button) {
        switch (button) {
            case 1:
                return !leftDown;
            case 2:
                return !midDown;
            case 3:
                return !rightDown;
        }
        return false;
    }

    public synchronized boolean canHold(int button) {
        switch (button) {
            case 1:
                return !leftDown;
            case 2:
                return !midDown;
            case 3:
                return !rightDown;
        }
        return false;
    }

    public synchronized boolean canRelease(int button) {
        switch (button) {
            case 1:
                return leftDown;
            case 2:
                return midDown;
            case 3:
                return rightDown;
        }
        return false;
    }

}
