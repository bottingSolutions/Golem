/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.api;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import solutions.botting.golem.Golem;
import solutions.botting.golem.shape.ShapeBounds;
import solutions.botting.golem.shape.ShapeType;
import static solutions.botting.golem.shape.ShapeType.CIRCLE;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class ShapeApi extends Api {

    public ShapeApi(Golem golem) {
        super(golem);
    }

    public List<ShapeBounds> getShapes() {
        ArrayList<ShapeBounds> shapes = new ArrayList<>();
        Rectangle bounds = golem.getBotBounds();
        BufferedImage image = golem.getRobot().createScreenCapture(bounds);
        Mat gray = new Mat();
        Mat bw = new Mat();
        Mat hierarchy = new Mat();

        Mat source = bufferedImageToMat(image);
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(gray, bw, new Size(3, 3));
        Imgproc.Canny(gray, bw, 80, 240);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat contourImage = bw.clone();
        Imgproc.findContours(
                contourImage,
                contours,
                hierarchy,
                Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        // loop over all found contours
        for (MatOfPoint cnt : contours) {
            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());

            // approximates a polygonal curve with the specified precision
            Imgproc.approxPolyDP(
                    curve,
                    approxCurve,
                    0.02 * Imgproc.arcLength(curve, true),
                    true
            );

            int numberVertices = (int) approxCurve.total();
            double contourArea = Imgproc.contourArea(cnt);

            // ignore to small areas
            if (Math.abs(contourArea) < 1 // || !Imgproc.isContourConvex(
                    ) {
                continue;
            }

            // triangle detection
            if (numberVertices == 3) {
                shapes.add(new ShapeBounds(ShapeType.TRIANGLE, this.getBoundingRectangleWithinBotBounds(cnt)));
            }

            // rectangle, pentagon and hexagon detection
            if (numberVertices > 6) {
                shapes.add(new ShapeBounds(ShapeType.POLYGON, this.getBoundingRectangleWithinBotBounds(cnt)));
            }
            if (numberVertices >= 4 && numberVertices <= 6) {
                List<Double> cos = new ArrayList<>();
                for (int j = 2; j < numberVertices + 1; j++) {
                    cos.add(
                            angle(
                                    approxCurve.toArray()[j % numberVertices],
                                    approxCurve.toArray()[j - 2],
                                    approxCurve.toArray()[j - 1]
                            )
                    );
                }
                Collections.sort(cos);

                double mincos = cos.get(0);
                double maxcos = cos.get(cos.size() - 1);

                // rectangle detection
                if (numberVertices == 4
                        && mincos >= -0.1 && maxcos <= 0.3) {
                    shapes.add(new ShapeBounds(ShapeType.RECTANGLE, this.getBoundingRectangleWithinBotBounds(cnt)));

                } // pentagon detection
                else if (numberVertices == 5
                        && mincos >= -0.34 && maxcos <= -0.27) {
                    shapes.add(new ShapeBounds(ShapeType.PENTAGON, this.getBoundingRectangleWithinBotBounds(cnt)));

                } // hexagon detection
                else if (numberVertices == 6
                        && mincos >= -0.55 && maxcos <= -0.45) {
                    shapes.add(new ShapeBounds(ShapeType.HEXAGON, this.getBoundingRectangleWithinBotBounds(cnt)));

                }
            } // circle detection
            else {
                Rect r = Imgproc.boundingRect(cnt);

                int radius = r.width / 2;

                if (Math.abs(
                        1 - (r.width / r.height)
                ) <= 0.2
                        && Math.abs(
                                1 - (contourArea / (Math.PI * radius * radius))
                        ) <= 0.2) {

                    shapes.add(new ShapeBounds(ShapeType.CIRCLE, this.getBoundingRectangleWithinBotBounds(cnt)));
                }

            }

        }

        return shapes;

    }

    public List<ShapeBounds> findShapesWithSize(ShapeType type, int width, int height, int tolerance) {
        List<ShapeBounds> shapes = getShapes();
        
        return shapes.stream().filter(s -> s.getType().equals(type) && Math.abs(s.getBounds().getWidth() - width) <= tolerance && Math.abs(s.getBounds().getHeight() - height) <= tolerance).collect(Collectors.toList());

    }
    public List<ShapeBounds> findShapesWithSizeWithinArea(ShapeType type, int width,int height, int tolerance, int x1,int y1,int x2,int y2){
         List<ShapeBounds> shapes = getShapes();
         shapes.forEach(System.out::println);
         if(type.equals(CIRCLE)){
               return shapes.stream().filter(s -> s.getType().equals(type)  && s.getCenterX() >= x1 && s.getCenterX() <= x2 && s.getCenterY() >= y1 && s.getCenterY() <= y2).collect(Collectors.toList());
         }
        return shapes.stream().filter(s -> s.getType().equals(type) && Math.abs(s.getBounds().getWidth() - width) <= tolerance && Math.abs(s.getBounds().getHeight() - height) <= tolerance && s.getCenterX() >= x1 && s.getCenterX() <= x2 && s.getCenterY() >= y1 && s.getCenterY() <= y2).collect(Collectors.toList());
       
    }

    /**
     * Helper function to find a cosine of angle between vectors from pt0->pt1
     * and pt0->pt2
     */
    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2)
                / Math.sqrt(
                        (dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10
                );
    }


}
