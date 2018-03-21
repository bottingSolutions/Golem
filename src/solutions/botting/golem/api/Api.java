/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.api;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import solutions.botting.golem.Golem;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class Api {

    protected Golem golem;
    private final Random random;

    public Api(Golem golem) {
        this.golem = golem;
        this.random = new Random();
    }

    public BufferedImage getBufferedImageOfBotBounds() {
        return getBufferedImageOfBounds(golem.getBotBounds());
    }

    private BufferedImage getBufferedImageOfBounds(Rectangle r) {
        return golem.getRobot().createScreenCapture(r);
    }

    public Rectangle getBoundsWithinBotBounds(Rectangle r) {
        Rectangle bounds = golem.getBotBounds();
        Double x = bounds.getX() + r.getX();
        Double y = bounds.getY() + r.getY();
        Double width = r.getWidth();
        Double height = r.getHeight();
        return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
    }

    public Rectangle getBoundingRectangleWithinBotBounds(MatOfPoint contour) {
        Rect conBounds = Imgproc.boundingRect(contour);
        Rectangle botBounds = golem.getBotBounds();
        Point p = conBounds.tl();
        Double x = p.x - botBounds.getX();
        Double y = p.y - botBounds.getY();

        return new Rectangle(x.intValue(), y.intValue(), conBounds.width, conBounds.height);
    }

    public Point getRandomPointInBounds(int x, int y, int width, int height) {

        int px = x + random.nextInt(width);
        int py = y + random.nextInt(height);

        return new Point(px, py);
    }

    public Point getRandomPointInBounds(Rectangle r) {
        Double x = r.getX();
        Double y = r.getY();
        Double w = r.getWidth();
        Double h = r.getHeight();
        return getRandomPointInBounds(x.intValue(), y.intValue(), w.intValue(), h.intValue());
    }

    protected Mat bufferedImageToMat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i]) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            }
        } else {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i]) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
            }
        }
        out.put(0, 0, data);
        return out;
    }

}
